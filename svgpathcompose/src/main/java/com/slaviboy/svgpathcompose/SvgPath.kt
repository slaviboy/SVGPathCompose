/*
* Copyright (C) 2022 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.svgpathcompose

import android.graphics.Matrix
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.slaviboy.svgpathcompose.Command.Companion.TYPE_C
import com.slaviboy.svgpathcompose.Command.Companion.TYPE_M
import com.slaviboy.svgpathcompose.Command.Companion.TYPE_Z
import com.slaviboy.svgpathcompose.CommandOperations.absolutize
import com.slaviboy.svgpathcompose.CommandOperations.normalize
import com.slaviboy.svgpathcompose.CommandOperations.parse
import com.slaviboy.svgpathcompose.CommandOperations.toUpperCase

/**
 * Class that gets svg path commands data, and translate it to canvas commands,
 * that can be used to draw svg path using its data into Android Canvas.
 * @param data raw path data as string
 * @param matrix matrix with transformations that will be applied to the path
 */
class SvgPath(
    var data: String,
    var width: Float,
    var height: Float
) {
    companion object {
        const val AUTO_SIZE = -1f
    }

    val initialCommands: ArrayList<Command>         // initial path commands that are extracted from the data string
    val absolutizedCommands: ArrayList<Command>     // absolutized commands converted from the initial commands, that means commands with lowercase 'v', 'h', 's'.. are converted to absolute command with upper case 'V', 'H', 'S'..
    val normalizedCommands: ArrayList<Command>      // normalized commands converted from the absolutized commands, that means converted from any command 'V', 'H', 'S' to 'C' command
    var isUpdated: Boolean                          // if path is updated and calling the getter for 'bound' for the path should generated again or use previous value
    var matrix: Matrix = Matrix()

    var scaleFact: Float = 1f
    var translateX: Float = 0f
    var translateY: Float = 0f

    // the boundary box that surrounds the path
    var bound: Rect = Rect(0f, 0f, 0f, 0f)
        get() {
            if (isUpdated) {
                isUpdated = false
                field = if (normalizedCommands.size > 0) {

                    var left = Float.POSITIVE_INFINITY
                    var top = Float.POSITIVE_INFINITY
                    var right = Float.NEGATIVE_INFINITY
                    var bottom = Float.NEGATIVE_INFINITY

                    // find the min and max x,y values that will be the bound of the path
                    for (i in normalizedCommands.indices) {
                        val coordinates = normalizedCommands[i].coordinates
                        for (j in 0 until coordinates.size / 2) {

                            // set min X for left, max X for right
                            if (coordinates[j * 2] < left) {
                                left = coordinates[j * 2]
                            }
                            if (coordinates[j * 2] > right) {
                                right = coordinates[j * 2]
                            }

                            // set min Y for top, max Y for bottom
                            if (coordinates[j * 2 + 1] < top) {
                                top = coordinates[j * 2 + 1]
                            }
                            if (coordinates[j * 2 + 1] > bottom) {
                                bottom = coordinates[j * 2 + 1]
                            }
                        }
                    }
                    Rect(left, top, right, bottom)
                } else Rect(0f, 0f, 0f, 0f)
            }
            return field
        }

    init {
        initialCommands = parse(data)
        absolutizedCommands = absolutize(initialCommands)
        normalizedCommands = normalize(absolutizedCommands)

        // keep order!
        isUpdated = true
        setUpMatrix()
    }

    /**
     * Set up matrix to fit the SVG path inside the width,height bound once the
     * command coordinates are mapped using the matrix
     */
    private fun setUpMatrix() {
        val bound = bound
        val scaleFactWidth = width / bound.width
        val scaleFactHeight = height / bound.height
        scaleFact = min(scaleFactWidth, scaleFactHeight)
        val isFitHeight = (scaleFactHeight < scaleFactWidth)
        if (isFitHeight) {
            translateX = (width - (bound.width * scaleFact)) / 2f
            translateY = 0f
        } else {
            translateX = 0f
            translateY = (height - (bound.height * scaleFact)) / 2f
        }
        matrix = Matrix().apply {
            setScale(scaleFact, scaleFact)
            postTranslate(translateX, translateY)
        }
    }

    /**
     * Check whether the path is closed
     */
    fun isClosed(): Boolean {
        // check if last command type is Z for closed path
        val part = absolutizedCommands[absolutizedCommands.size - 1]
        return part.type.toUpperCase() == TYPE_Z
    }

    /**
     * Generate the graphic path from the existing normalized commands, that are cubic bezier curves
     * and return it. Each normalized command that is either 'M' or 'C' type.
     * @param path existing android graphic path
     */
    fun generatePath(path: Path = Path()): Path {
        path.reset()

        if (normalizedCommands.size > 0) {

            // for each normalized command that is either 'M' or 'C' type
            for (command in normalizedCommands) {
                val type = command.type.toUpperCase()
                val normalizedCoordinates = command.transform(matrix)

                if (type == TYPE_M) {
                    path.moveTo(normalizedCoordinates[0], normalizedCoordinates[1])
                } else if (type == TYPE_C) {
                    // analogue to a bezier curve
                    path.cubicTo(
                        normalizedCoordinates[0], normalizedCoordinates[1],
                        normalizedCoordinates[2], normalizedCoordinates[3],
                        normalizedCoordinates[4], normalizedCoordinates[5]
                    )
                }
            }

            // close path
            if (isClosed()) {
                path.close()
            }
        }
        return path
    }

    /**
     * Get the coordinates from the array list containing the initial commands
     */
    fun getInitialCoordinates(): ArrayList<FloatArray> {
        return Command.getCoordinates(initialCommands)
    }

    /**
     * Get the coordinates from the array list containing the absolutized commands
     */
    fun getAbsolutizedCoordinates(): ArrayList<FloatArray> {
        return Command.getCoordinates(absolutizedCommands)
    }

    /**
     * Get the coordinates from the array list containing the normalized commands
     */
    fun getNormalizedCoordinates(): ArrayList<FloatArray> {
        return Command.getCoordinates(normalizedCommands)
    }

    /**
     * Get the transformed coordinates from given matrix, if none is give the matrix for current
     * object is used.
     * @param matrix transformation matrix whit the transformation that will be applied to the path
     */
    fun getTransformedCoordinates(matrix: Matrix = this.matrix): ArrayList<FloatArray> {

        val transformedCoordinates = ArrayList<FloatArray>()
        normalizedCommands.forEach {
            transformedCoordinates.add(it.transform(matrix))
        }
        return transformedCoordinates
    }
}

