package com.jj.haha.jrouter

import com.jj.haha.jrouter.annotation.RouterService

@RouterService(
    interfaces = [ITest::class],
    key = ["====================="]
)
class Test : ITest{
}