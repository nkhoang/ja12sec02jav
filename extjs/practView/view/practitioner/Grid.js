Ext.define('practView.view.practitioner.Grid', {
  extend:'Ext.gird.Panel',
  alias:'widget.practitionerGrid',

  initComponent:function () {
    this.store = {
      fields:['name', 'email'],
      data:[
        {name:'Ed', email:'ed@sencha.com'},
        {name:'Tommy', email:'tommy@sencha.com'}
      ]
    };

    this.queryMode = 'local';
    this.displayField = 'name';
    this.valueField = 'abbr';

    this.callParent(arguments);
  }
});