Ext.define('demo.view.theme.Combo', {
  extend:'Ext.form.field.ComboBox',
  alias:'widget.themeCombo',
  itemId: 'themeCombo',
  emptyText: 'Select a theme',

  initComponent:function () {
    this.store = {
      fields:['name', 'fileName'],
      data:[
        {name:'default', fileName:'my-ext-theme.css'},
        {name:'shadow', fileName:'shadow-theme.css'}
      ]
    };

    this.queryMode = 'local';
    this.displayField = 'name';
    this.valueField = 'fileName';

    this.callParent(arguments);
  }
});