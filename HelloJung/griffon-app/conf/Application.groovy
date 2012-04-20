application {
    title = 'HelloJung'
    startupGroups = ['helloJung']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "helloJung"
    'helloJung' {
        model      = 'hellojung.HelloJungModel'
        view       = 'hellojung.HelloJungView'
        controller = 'hellojung.HelloJungController'
    }

}
