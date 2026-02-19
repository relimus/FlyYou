package io.relimus.flyyou.xposed.base

import android.content.Context
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

abstract class XBridge : YukiBaseHooker() {
    override fun onHook() {
        onAppLifecycle {
            onCreate {
                withProcess(mainProcessName) {
                    onBaseHook(appContext!!, appClassLoader!!)
                }
            }
        }
    }
    protected fun String.toTargetClass() = this.toClass(appClassLoader!!)
    abstract fun onBaseHook(ctx: Context, loader: ClassLoader)
}
