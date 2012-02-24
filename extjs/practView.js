Ext.Loader.setConfig({
  enabled:true,
  paths:{
    'practView':'practView'
  }
});

Ext.application({
  name:'practView',

  appFolder:'practView',

  controllers:[
    'Practitioner'
  ],

  initThemeChanger:function () {
    var cp = new Ext.state.CookieProvider();
    if (cp.get('theme') != '') {
      Ext.util.CSS.swapStyleSheet('theme', 'resources/css/' + cp.get('theme'));
    }
    else {
      var theme_file = 'my-ext-theme.css';
      Ext.util.CSS.swapStyleSheet('theme', '/css/' + theme_file);
      cp.set('theme', theme_file);
    }
  },

  launch:function () {
    // this.initThemeChanger();

    Ext.create('Ext.container.Viewport', {
      layout:'fit',
      renderTo:'page-content',
      items:[
        //{
//          xtype:'themeCombo'
//        },
        {
          xtype:'panel',
          id:'main-page',
          itemId:'page',
          margin:'0px 10px',
          layout:'anchor',
          border:true,
          bodyCls:'no-border',
          cls:'full-rounded',
          dockedItems:[
            {
              // replace header
              xtype:'toolbar',
              dock:'top',
              cls:'panel-header',
              margin:0,
              layout:'fit',
              style:{
                border:'none'
              },
              items:[
                {
                  // header container
                  xtype:'container',
                  height:25,
                  cls:'panel-header-container',
                  layout:'anchor',
                  items:[
                    // header title.=
                    {
                      xtype:'label',
                      text:'Home',
                      cls:'header-title'
                    },
                    {
                      xtype:'box',
                      cls:'float-right',
                      autoEl:{
                        tag:'a',
                        html:'Logout',
                        href:'http://www.google.com'
                      }
                    },
                    {
                      xtype:'label',
                      text:'|',
                      cls:'float-right divider'
                    },
                    {
                      xtype:'box',
                      cls:'float-right',
                      autoEl:{
                        tag:'a',
                        html:'User Settings',
                        href:'http://www.google.com'
                      }
                    },
                    {
                      xtype:'label',
                      text:'|',
                      cls:'float-right divider'
                    },
                    {
                      xtype:'box',
                      cls:'float-right',
                      autoEl:{
                        tag:'a',
                        html:'Help',
                        href:'http://www.google.com'
                      }
                    }
                  ]
                }
              ]
            }
          ],
          items:[
            {
              xtype:'practitionerGrid',
              height:400
            }
          ]
        }
      ]
    });
  }
});