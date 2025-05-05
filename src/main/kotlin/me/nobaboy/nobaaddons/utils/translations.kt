@file:Suppress("unused")

package me.nobaboy.nobaaddons.utils

import net.minecraft.text.MutableText
import net.minecraft.text.Text

// uses of `tr` are replaced with `trResolved` at compile time
fun tr(key: String, default: String): MutableText = error("Compiler plugin did not run")
fun trResolved(key: String, vararg args: Any): MutableText = Text.stringifiedTranslatable(key, *args)
