package griffonhello

//application(title: 'GriffonHello',
//  preferredSize: [320, 240],
//  pack: true,
//  //location: [50,50],
//  locationByPlatform:true,
//  iconImage: imageIcon('/griffon-icon-48x48.png').image,
//  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
//               imageIcon('/griffon-icon-32x32.png').image,
//               imageIcon('/griffon-icon-16x16.png').image]) {
//    // add content here
//    label('Content Goes Here') // delete me
//}

application(title:'GriffonHello', pack:true, locationByPlatform:true) {
    panel(border:emptyBorder(6)) {
        borderLayout()

        scrollPane(constraints:CENTER) {
            textArea(text:bind(target:model, targetProperty:'scriptSource'),
                    enabled: bind {model.enabled},
                    columns:40, rows:10)
        }

        hbox(constraints:SOUTH) {
            button("Execute", actionPerformed:controller.&executeScript,
                    enabled: bind {model.enabled})
            hstrut(5)
            label("Result:")
            hstrut(5)
            label(text:bind {model.scriptResult})
        }
    }
}

