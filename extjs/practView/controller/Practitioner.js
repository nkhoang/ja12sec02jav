Ext.define('practView.controller.Practitioner', {
  extend:'Ext.app.Controller',

  views:[
    'practitioner.Grid',
    'practitioner.detailPanel',
    'practitioner.fullViewPanel'
  ],

  stores:['Practitioner'],
  models:['Practitioner'],

  refs:[
    {
      ref:'practitionerGrid',
      selector:'practitionerGrid'
    },
    {
      ref: 'practDetailPanel',
      selector: 'practDetailPanel'
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
        render:this.onRendered,
        itemclick: function(view, record, htmlItem, index, eventE, eOpts) {
          me.getPractDetailPanel().updateDetail(record.data);
        },
        itemdblclick: function(view, record, htmlItem, index, e, eOpts) {
          var fullViewPanel = Ext.create('practView.view.practitioner.fullViewPanel');
          fullViewPanel.flex = 1;
          fullViewPanel.height = 100;
          fullViewPanel.width = 200;
          fullViewPanel.region = 'center';
/*


          // replace content
          var parentCnt = Ext.getCmp('main-page');
          parentCnt.removeAll(true);
          parentCnt.add(fullViewPanel);
          parentCnt.doLayout();
*/
          var parentCnt = Ext.getCmp('main-page');
          parentCnt.remove(me.getPractDetailPanel(), true);
          parentCnt.remove(me.getPractitionerGrid(), true);
          parentCnt.doLayout(false, true);
          parentCnt.add(fullViewPanel);

          parentCnt.doLayout(false, true);
          parentCnt.refresh();
        }
      }
    });
  },

  onRendered:function (eComp, eOpts) {
    // show loading, hot fix for show loading
    eComp.showLoading();
  }
});