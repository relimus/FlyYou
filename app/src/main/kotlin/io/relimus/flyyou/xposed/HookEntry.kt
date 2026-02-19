package io.relimus.flyyou.xposed

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import io.relimus.flyyou.xposed.hooks.pkginstaller.DisableInstallCheck
import io.relimus.flyyou.xposed.hooks.settings.SettingGMSActivity
import io.relimus.flyyou.xposed.hooks.systemuitools.RemoveGamePanelGift

@InjectYukiHookWithXposed(entryClassName = "Entry")
object HookEntry : IYukiHookXposedInit {
    override fun onInit() = configs {
        debugLog { tag = "FlyYou" }
        isDebug = false
        isEnableDataChannel = false
    }
    override fun onHook() = encase {
        loadApp("com.flyme.systemuitools", RemoveGamePanelGift)
        loadApp("com.android.settings", SettingGMSActivity)
        loadApp("com.android.packageinstaller", DisableInstallCheck)
    }
}