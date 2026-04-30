package io.relimus.flyyou.xposed.hooks.systemui

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import io.relimus.flyyou.xposed.base.XBridge

object HideBatteryIcon : XBridge() {

    override fun onBaseHook(ctx: Context, loader: ClassLoader) {
        runCatching {
            val batteryClass = "com.flyme.statusbar.battery.FlymeBatteryMeterView".toTargetClass()

            batteryClass.resolve().firstMethod {
                name = "onMeasure"
                parameters(Int::class.java, Int::class.java)
            }.hook {
                replaceUnit {
                    val view = instance<View>()
                    val viewResolver = view.asResolver()
                    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { margins ->
                        if (
                            margins.leftMargin != 0 ||
                            margins.topMargin != 0 ||
                            margins.rightMargin != 0 ||
                            margins.bottomMargin != 0 ||
                            margins.marginStart != 0 ||
                            margins.marginEnd != 0
                        ) {
                            margins.setMargins(0, 0, 0, 0)
                            margins.marginStart = 0
                            margins.marginEnd = 0
                            view.layoutParams = margins
                        }
                    }

                    val isCharging = viewResolver.firstField { name = "mCharging" }.get<Boolean>() == true
                    val isPlugged = viewResolver.firstField { name = "mLastPlugged" }.get<Boolean>() == true
                    val isQuickCharging = viewResolver.firstField { name = "mQuickCharging" }.get<Boolean>() == true
                    val lightning = viewResolver.firstField {
                        name = if (isQuickCharging) "mBatteryLightningQuick" else "mBatteryLightning"
                    }.get<Drawable>()

                    val width = lightning?.intrinsicWidth?.coerceAtLeast(0)
                        ?.takeIf { isCharging || isPlugged } ?: 0

                    val drawable = viewResolver.firstField { name = "mDrawable" }.get<Drawable>()
                    val height = listOf(
                        drawable?.intrinsicHeight ?: 0,
                        lightning?.intrinsicHeight ?: 0,
                        view.minimumHeight
                    ).max()

                    viewResolver.firstMethod {
                        name = "setMeasuredDimension"
                        parameters(Int::class.java, Int::class.java)
                        superclass()
                    }.invoke(width, height)
                }
            }

            batteryClass.resolve().firstMethod {
                name = "onDraw"
                parameters(Canvas::class.java)
            }.hook {
                replaceUnit {
                    val view = instance<View>()
                    val viewResolver = view.asResolver()
                    (view.layoutParams as? ViewGroup.MarginLayoutParams)?.let { margins ->
                        if (
                            margins.leftMargin != 0 ||
                            margins.topMargin != 0 ||
                            margins.rightMargin != 0 ||
                            margins.bottomMargin != 0 ||
                            margins.marginStart != 0 ||
                            margins.marginEnd != 0
                        ) {
                            margins.setMargins(0, 0, 0, 0)
                            margins.marginStart = 0
                            margins.marginEnd = 0
                            view.layoutParams = margins
                        }
                    }

                    val isCharging = viewResolver.firstField { name = "mCharging" }.get<Boolean>() == true
                    val isPlugged = viewResolver.firstField { name = "mLastPlugged" }.get<Boolean>() == true

                    if (!isCharging && !isPlugged) return@replaceUnit

                    val canvas = args(0).cast<Canvas>()!!

                    val isQuickCharging = viewResolver.firstField { name = "mQuickCharging" }.get<Boolean>() == true

                    val lightning = viewResolver.firstField {
                        name = if (isQuickCharging) "mBatteryLightningQuick" else "mBatteryLightning"
                    }.get<Drawable>() ?: return@replaceUnit

                    lightning.clearColorFilter()

                    lightning.alpha =
                        viewResolver.firstField { name = "mBatteryLightAlpha" }.get<Int>()?.takeIf { it > 0 } ?: 255

                    val isInternational =
                        "android.os.BuildExt".toTargetClass().asResolver().firstMethod {
                            name = "isProductInternational"
                        }.invoke<Boolean>() == true

                    if (isInternational && view.resources.configuration.layoutDirection == 1) {
                        lightning.setBounds(canvas.width - lightning.intrinsicWidth, 0, canvas.width, canvas.height)
                    } else {
                        lightning.setBounds(0, 0, lightning.intrinsicWidth, canvas.height)
                    }
                    lightning.draw(canvas)
                }
            }
        }
    }
}
