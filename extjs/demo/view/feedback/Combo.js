Ext.define('demo.view.feedback.Combo', {
  extend:'Ext.form.field.ComboBox',
  alias:'widget.feedbackCombo',
  anchor: '-30px',

  emptyText: 'Select Feedback Type',

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