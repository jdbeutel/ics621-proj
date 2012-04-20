application {
    title = 'GriffonHello'
    startupGroups = ['griffonHello']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "griffon-hello"
    'griffon-hello' {
        model      = 'griffon.hello.GriffonHelloModel'
        view       = 'griffon.hello.GriffonHelloView'
        controller = 'griffon.hello.GriffonHelloController'
    }

}
