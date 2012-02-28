Ext.define('practView.controller.Practitioner', {
  extend:'Ext.app.Controller',

  views:[
    'practitioner.practGrid',
    'practitioner.practDetailedPanel',
    'practitioner.detailsViewPanel',
    'practitioner.defaultViewPanel'
  ],

  stores:['Practitioner'],
  models:['Practitioner'],

  refs:[
    {
      ref:'practGrid',
      selector:'practGrid'
    },
    {
      ref:'practDetailedPanel',
      selector:'practDetailedPanel'
    },
    {
      ref: 'detailsViewPanel',
      selector: 'detailsViewPanel'
    },
    {
      ref: 'defaultViewPanel',
      selector: 'defaultViewPanel'
    }
  ],

  init:function () {
    var me = this;
    this.getPractitionerStore().load({
      callback:function (records, operation, success) {
        // unmask
        me.getPractGrid().hideLoading();
      }});

    this.control({
      'practGrid':{
        render:this.onRendered,
        itemclick:function (view, record, htmlItem, index, eventE, eOpts) {
          me.getPractDetailedPanel().updateDetail(record.data);
        },
        itemdblclick:function (view, record, htmlItem, index, e, eOpts) {
          var detailsViewPanel = Ext.create('practView.view.practitioner.detailsViewPanel');
          var parentCnt = Ext.getCmp('main-page');
          parentCnt.removeAll();
          detailsViewPanel.addListener('render', function() {
            this.updateDetail(record.data);
          });
          parentCnt.add(detailsViewPanel);
          parentCnt.doLayout();
        }
      }
    });
  },

  onRendered:function (eComp, eOpts) {
    // show loading, hot fix for show loading
    eComp.showLoading();
  }
});