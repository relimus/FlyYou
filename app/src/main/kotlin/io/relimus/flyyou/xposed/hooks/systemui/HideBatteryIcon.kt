package io.relimus.flyyou.xposed.hooks.systemui

import android.content.Context
import android.graphics.Canvas
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import io.relimus.flyyou.xposed.base.XBridge

object HideBatteryIcon : XBridge() {
    override fun onBaseHook(ctx: Context, loader: ClassLoader) {
        val batteryClass = "com.flyme.statusbar.battery.FlymeBatteryMeterView".toTargetClass()

        val setMeasuredDimension = View::class.java.asResolver().firstMethod {
            name = "setMeasuredDimension"
            parameterCount(2)
        }

        batteryClass.asResolver().firstMethod {
            name = "onDraw"
            parameters(Canvas::class.java)
        }.hook {
            replaceUnit {}
        }

        batteryClass.asResolver().firstMethod {
            name = "onAttachedToWindow"
            emptyParameters()
        }.hook {
            after {
                (instance as View).visibility = View.GONE
            }
        }

        batteryClass.asResolver().firstMethod {
            name = "onMeasure"
            parameterCount(2)
        }.hook {
            replaceUnit {
                setMeasuredDimension.copy().of(instance<Class<View>>()).invoke(0, 0)
            }
        }
    }
}