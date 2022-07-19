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

package dev.misasi.giancarlo.collections

fun <K, V : Any> List<Map<K, V>>.mergeReduce(reduce: (V, V) -> V): Map<K, V> =
    mutableMapOf<K, V>().mergeReduceInPlace(this, reduce)

fun <K, V : Any> Map<K, V>.mergeReduce(others: List<Map<K, V>>, reduce: (V, V) -> V): Map<K, V> =
    toMutableMap().mergeReduceInPlace(others, reduce)

fun <K, V : Any> MutableMap<K, V>.mergeReduceInPlace(others: List<Map<K, V>>, reduce: (V, V) -> V): MutableMap<K, V> =
    apply { others.forEach { other -> other.forEach { merge(it.key, it.value, reduce) } } }

fun <K, V : Any> Map<K, V>.mergeReduce(vararg others: Map<K, V>, reduce: (V, V) -> V): Map<K, V> =
    toMutableMap().apply { others.forEach { other -> other.forEach { merge(it.key, it.value, reduce) } } }

fun <K, V : Any> MutableMap<K, V>.mergeReduceInPlace(vararg others: Map<K, V>, reduce: (V, V) -> V): MutableMap<K, V> =
    apply { others.forEach { other -> other.forEach { merge(it.key, it.value, reduce) } } }