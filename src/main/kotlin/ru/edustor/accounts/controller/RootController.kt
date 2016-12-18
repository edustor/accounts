package ru.edustor.accounts.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.edustor.accounts.util.EdustorVersionInfoHolder

@Controller
class RootController(val versionHolder: EdustorVersionInfoHolder) {
    @RequestMapping("/")
    @ResponseBody
    fun root(): String {
        return "Welcome to Edustor Accounts Microservice v${versionHolder.version}. <br><br> " +
                "There is no human interface so far. If you're developer, please refer to Edustor API Documentation"
    }
}