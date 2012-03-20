Ext.Loader.setConfig({
   enabled:true,
   paths:{
      'practView':'practView'
   }
});
Ext.require([
   'practView.event.EventManager',
   'practView.plugin.RowExpander'
]);


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
            {
               xtype: 'userManagement'
            }]
      });
   }
});