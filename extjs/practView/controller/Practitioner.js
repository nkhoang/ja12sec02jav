Ext.define('practView.controller.Practitioner', {
  extend:'Ext.app.Controller',

  views:[
    'practitioner.Grid'
  ],

  stores: ['Practitioner'],
  models: ['Practitioner'],

  init:function () {
    this.getPractitionerStore().load();

    this.control({

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