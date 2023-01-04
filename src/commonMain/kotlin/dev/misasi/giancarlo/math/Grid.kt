/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.misasi.giancarlo.math

class Grid<T>(val width: Int, val height: Int, init: (index: Int) -> T) : Iterable<Cell<T>> {
    val size: Int = width * height
    private val data = MutableList(size, init)

    fun at(index: Int): T = data[index]
    fun at(x: Int, y: Int): T = data[index(x, y)]

    fun replace(index: Int, value: T) = data.set(index, value)
    fun replace(x: Int, y: Int, value: T) = data.set(index(x, y), value)

    fun neighbor(index: Int, direction: Direction): Cell<T>? = neighborIndex(index, direction)?.let { Cell(this, it, at(it)) }
    fun neighbor(x: Int, y: Int, direction: Direction): Cell<T>? = neighbor(index(x, y), direction)

    fun neighbors(index: Int, vararg directions: Direction): List<Cell<T>> = directions.mapNotNull { neighbor(index, it) }
    fun neighbors(x: Int, y: Int, vararg directions: Direction): List<Cell<T>> = neighbors(index(x, y), *directions)
    fun neighbors(index: Int): List<Cell<T>> = neighbors(index, *Direction.values())
    fun neighbors(x: Int, y: Int): List<Cell<T>> = neighbors(index(x, y), *Direction.values())

    private fun index(x: Int, y: Int) = y * width + x

    private fun neighborIndex(index: Int?, direction: Direction): Int? {
        if (index == null || index < 0 || index >= size) {
            return null
        }

        return when (direction) {
            Direction.UP -> if (index >= width) index - width else null
            Direction.LEFT -> if (index % width != 0) index - 1 else null
            Direction.DOWN -> (index + width).run { if (this < size) this else null }
            Direction.RIGHT -> (index + 1).run { if (this % width != 0) this else null }
            Direction.UP_LEFT -> neighborIndex(neighborIndex(index, Direction.UP), Direction.LEFT)
            Direction.UP_RIGHT -> neighborIndex(neighborIndex(index, Direction.UP), Direction.RIGHT)
            Direction.DOWN_LEFT -> neighborIndex(neighborIndex(index, Direction.DOWN), Direction.LEFT)
            Direction.DOWN_RIGHT -> neighborIndex(neighborIndex(index, Direction.DOWN), Direction.RIGHT)
            else -> null
        }
    }

    override fun iterator(): Iterator<Cell<T>> = CellIterator(this)
}

data class Cell<T>(private val grid: Grid<T>, val index: Int, val value: T) {
    val x = index % grid.width
    val y = index / grid.height
}

class CellIterator<T>(private val grid: Grid<T>) : Iterator<Cell<T>> {
    private var index = 0
    override fun hasNext(): Boolean = index < grid.size
    override fun next(): Cell<T> = Cell(grid, index, grid.at(index)).also { index ++ }
}