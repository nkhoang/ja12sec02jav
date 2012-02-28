Ext.define('practView.view.practitioner.practDetailedPanel', {
  extend:'Ext.panel.Panel',
  alias:'widget.practDetailedPanel',
  practDetailTplMarkup:[
    '<b>Name:</b> {name}<br />',
    '<b>NPI:</b> {npi}<br />',
    '<b>Type:</b> {type}<br />',
    '<b>Address:</b> {address}<br />',
    '<b>Status:</b> {status}<br />',
    '<b>Vendible:</b> {vendible}<br />',
    '<b>Number of Affiliations:</b> {numAffil}<br />'
  ],
  startingMarkup:'Please select a practitioner to see additional details.',
  initComponent:function () {
    this.tpl = Ext.create('Ext.Template', this.practDetailTplMarkup);
    this.html = this.startingMarkup;

    this.bodyStyle = {
      background:'white'
    };
    this.callParent(arguments);
  },

  updateDetail:function (data) {
    this.tpl.overwrite(this.body, data);
  }
})