Ext.define('practView.view.practitioner.detailsViewPanel', {
  extend: 'Ext.panel.Panel',
  alias : 'widget.detailsViewPanel',

  dockedItems         : [
    {
      xtype   : 'toolbar',
      dock    : 'bottom',
      ui      : 'footer',
      defaults: {},
      items   : [
        { xtype: 'button',
          id   : 'detailViewBackBtn',
          text : 'back' }
      ]
    }
  ],
  practDetailTplMarkup: [
    '<b>Name:</b> {name}<br />',
    '<b>NPI:</b> {npi}<br />',
    '<b>Type:</b> {type}<br />',
    '<b>Address:</b> {address}<br />',
    '<b>Status:</b> {status}<br />',
    '<b>Vendible:</b> {vendible}<br />',
    '<b>Number of Affiliations:</b> {numAffil}<br />'
  ],
  startingMarkup      : 'Please select a practitioner to see additional details.',
  eventQueue          : [],
  initComponent       : function () {
    this.eventQueue = new Array();
    this.tpl = Ext.create('Ext.Template', this.practDetailTplMarkup);
    this.html = this.startingMarkup;
    this.addEvents('updateDetail');
    this.addListener('updateDetail', function () {
      var event = {};
      event.fn = this.updateDetail;
      event.args = arguments;

      this.eventQueue.push(event);
    });


    this.bodyStyle = {
      background: 'white'
    };

    this.callParent(arguments);
  },

  updateDetail     : function (data) {
    console.debug('Updating detail...');
    console.debug(data);
    this.tpl.overwrite(this.body, data);
    this.doLayout();
  },
  processEventQueue: function () {
    console.debug('Process event queue with size: [' + this.eventQueue.length + ']');
    do {
      var event = this.eventQueue.pop();
      event.fn.apply(this, event.args);
    }
    while (this.eventQueue.length != 0)
  }
})