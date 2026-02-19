package io.relimus.flyyou.xposed.hooks.pkginstaller

import android.content.Context
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import io.relimus.flyyou.xposed.base.XBridge

object DisableInstallCheck : XBridge() {
    override fun onBaseHook(ctx: Context, loader: ClassLoader) {
        val target = "com.android.packageinstaller.FlymePackageInstallerActivity".toTargetClass()
        target.asResolver().firstMethod {
            name = "startInstallScan"
        }.hook {
            replaceUnit {
                val storeInfo = instance.asResolver().firstField {
                    name = "mzStoreAppInfo"
                }.get<Any>()!!.asResolver()
                storeInfo.firstField { name = "icpStatus" }.set(true)
                storeInfo.firstField { name = "showConfirm" }.set(true)
                instance.asResolver().firstField {
                    name = "isAppSelfUpdate"
                }.set(true)
                instance.asResolver().firstMethod {
                    name = "updateViewForNewState"
                }.invoke(3)
                instance.asResolver().firstField {
                    name = "service_record_layout"
                }.get<View>()?.visibility = View.GONE
            }
        }
    }
}