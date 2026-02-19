package io.relimus.flyyou.xposed.hooks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.condition.type.Modifiers
import io.relimus.flyyou.xposed.base.XBridge

object SettingGMSActivity : XBridge() {
    override fun onBaseHook(ctx: Context, loader: ClassLoader) {
        val target = "com.meizu.settings.accessibility.FlymeAccessibilitySettings"
            .toTargetClass()

        target.asResolver().firstMethod {
            name = "onCreate"
        }.hook {
            before {
                target.asResolver().firstField {
                    type = Intent::class
                    modifiers(Modifiers.STATIC)
                }.get<Intent>()?.setComponent(
                    ComponentName(
                        "com.google.android.gms",
                        "com.google.android.gms.googlesettings.ui.GoogleSettingsActivity"
                    )
                )
            }
        }
    }
}
