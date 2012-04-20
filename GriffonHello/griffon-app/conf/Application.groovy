application {
    title = 'GriffonHello'
    startupGroups = ['griffonHello']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "griffonHello"
    'griffonHello' {
        model      = 'griffonhello.GriffonHelloModel'
        view       = 'griffonhello.GriffonHelloView'
        controller = 'griffonhello.GriffonHelloController'
    }

}
