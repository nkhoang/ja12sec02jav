Ext.define('practView.controller.Practitioner', {
  extend:'Ext.app.Controller',

  views:[
    'practitioner.Grid'
  ],

  stores:['Practitioner'],
  models:['Practitioner'],

  refs: [
    {
      ref: 'practitionerGrid',
      selector: 'practitionerGrid'
    }
  ],

  init:function () {
    var me = this;
    this.getPractitionerStore().load({
      callback:function (records, operation, success) {
        // unmask
        me.getPractitionerGrid().hideLoading();
      }});

    this.control({
      'practitionerGrid':{
        render:this.onRendered
      }
    });
  },

  onRendered:function (eComp, eOpts) {
    // show loading, hot fix for show loading
    eComp.showLoading();
  }
});