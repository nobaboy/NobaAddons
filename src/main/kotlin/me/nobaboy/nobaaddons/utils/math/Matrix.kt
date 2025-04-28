package me.nobaboy.nobaaddons.utils.math

import kotlin.math.abs

class Matrix(val data: Array<DoubleArray>) {
	val width: Int get() = data[0].size
	val height: Int get() = data.size

	operator fun get(row: Int): DoubleArray = data[row]
	operator fun set(row: Int, value: DoubleArray) {
		data[row] = value
	}

	fun copy(): Matrix = Matrix(Array(height) { row -> data[row].copyOf() })

	fun transpose(): Matrix = Matrix(Array(width) { column -> DoubleArray(height) { row -> data[row][column] } })

	fun inverse(): Matrix {
		require(height == width) { "Only square matrices are invertible" }

		val a = copy().data
		val b = identity(width).data

		for(column in 0 until width) {
			val maxRow = (column until height).maxByOrNull { abs(a[it][column]) }
				?: error("Matrix is singular and cannot be inverted")

			require(a[maxRow][column] != 0.0) { "Matrix is singular and cannot be inverted" }

			if(maxRow != column) {
				a.swapRows(column, maxRow)
				b.swapRows(column, maxRow)
			}

			val pivot = a[column][column]

			for(i in 0 until width) {
				a[column][i] /= pivot
				b[column][i] /= pivot
			}

			for(row in 0 until height) {
				if(row == column) continue

				val factor = a[row][column]
				for(i in 0 until width) {
					a[row][i] -= factor * a[column][i]
					b[row][i] -= factor * b[column][i]
				}
			}
		}

		return Matrix(b)
	}

	operator fun plus(other: Matrix): Matrix {
		require(width == other.width && height == other.height) { "Matrix dimensions must match for addition" }

		return Matrix(Array(height) { row ->
			DoubleArray(width) { column -> data[row][column] + other[row][column] }
		})
	}

	operator fun minus(other: Matrix): Matrix {
		require(width == other.width && height == other.height) { "Matrix dimensions must match for subtraction" }

		return Matrix(Array(height) { row ->
			DoubleArray(width) { column -> data[row][column] - other[row][column] }
		})
	}

	operator fun times(other: Matrix): Matrix {
		require(this.width == other.height) { "Inner matrix dimensions must match for multiplication" }

		val result = Array(height) { DoubleArray(other.width) }

		for(row in 0 until height) {
			for(column in 0 until other.width) {
				var sum = 0.0

				for(i in 0 until width) {
					sum += this[row][i] * other[i][column]
				}

				result[row][column] = sum
			}
		}

		return Matrix(result)
	}

	operator fun times(scalar: Double): Matrix =
		Matrix(Array(height) { row -> DoubleArray(width) { column -> data[row][column] * scalar } })

	override fun toString(): String = data.joinToString("\n") { row -> row.joinToString(" ") }

	companion object {
		fun identity(size: Int): Matrix =
			Matrix(Array(size) { row -> DoubleArray(size) { column -> if(row == column) 1.0 else 0.0 } })

		private fun Array<DoubleArray>.swapRows(i: Int, j: Int) {
			val temp = this[i]
			this[i] = this[j]
			this[j] = temp
		}
	}
}