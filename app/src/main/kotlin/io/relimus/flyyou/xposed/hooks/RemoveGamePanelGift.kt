package io.relimus.flyyou.xposed.hooks

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import io.relimus.flyyou.xposed.base.XBridge

object RemoveGamePanelGift : XBridge() {
    override fun onBaseHook(ctx: Context, loader: ClassLoader) {
        val target = "com.flyme.systemuitools.gameassiant.gamemode.layout.GmPanelMainHeaderLayout"
            .toTargetClass()

        target.asResolver().firstMethod {
            name = "onFinishInflate"
        }.hook {
            after {
                instance.asResolver().field {
                    type = ImageView::class
                }.last().get<View>()!!.visibility = View.GONE
            }
        }
    }
}
