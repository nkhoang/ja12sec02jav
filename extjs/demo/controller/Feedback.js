Ext.define('demo.controller.Feedback', {
  extend:'Ext.app.Controller',

  views:[
    'feedback.Combo',
    'feedback.Form',
    'theme.Combo'
  ],

  init:function () {
    this.control({
      'viewport > container':{
        render:this.onRendered
      },
      'themeCombo':{
        change:this.onChangeTheme
      }
    });
  },

  onRendered:function () {
  },

  onChangeTheme:function (field, newValue, oldValue) {
    var theme_file = newValue;
    if (theme_file != '') {
      Ext.util.CSS.swapStyleSheet('theme', 'resources/css/' + theme_file);
      new Ext.state.CookieProvider().set('theme', theme_file);
    }
  }
});