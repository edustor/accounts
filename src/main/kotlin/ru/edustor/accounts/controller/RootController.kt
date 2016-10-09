package ru.edustor.accounts.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.edustor.accounts.EdustorAccountsApplication

@Controller
class RootController {
    @RequestMapping("/")
    @ResponseBody
    fun root(): String {
        return "Welcome to Edustor Accounts Microservice v${EdustorAccountsApplication.VERSION}. <br><br> " +
                "There is no human interface so far. If you're developer, please refer to Edustor API Documentation"
    }
}