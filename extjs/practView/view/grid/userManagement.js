Ext.define('practView.view.grid.userManagement', {
   extend:'Ext.panel.Panel',
   requires:['practView.store.User'],
   alias:'widget.userManagement',
   margin:'10px',
   layout:'anchor',
   initComponent:function () {
      var me = this;
      var comboStore = Ext.create('Ext.data.Store', {
         data: [
            {
            "domain": "@healthmarketscience"
         }, {
            "domain": "@bayer"
         }],
         queryMode: 'local',
         fields: ['domain']
      });


      var store = Ext.create('practView.store.User', {
         listeners: {
            load: function(store, records, successful) {
               Ext.Array.each(records, function(record, index, countriesItSelf) {
                  // validate to make sure that it is in correct format
                  var email = record.raw.email;
                  if (Ext.data.validations.email(null, email)) {

                     var domain = email.substr(email.indexOf('@') + 1);
                     // add domain to comboStore
                     comboStore.add({
                        "domain": domain
                     })
                  }
               });
            }
         }
      });

      Ext.applyIf(me, {
         items:[
            {
               xtype:'container',
               flex: 1,
               items:[
                  {
                     xtype: 'combo',
                     itemId: 'cboFilter',
                     id: 'cboFilter',
                     fieldLabel: 'Filter by',
                     displayField: 'domain',
                     valueField: 'domain',
                     emptyText: 'Select one',
                     store: comboStore
                  }
               ]
            },
            {
               xtype:'grid',
               flex: 1,
               columns:[
                  { header:'First Name', dataIndex:'firstName', flex:1},
                  { header:'Last Name', dataIndex:'lastName', flex:1},
                  { header:'Email', dataIndex:'email', flex:2 },
                  { header:'Last Login', dataIndex:'lastLogin', flex:1 },
                  { header:'Disabled', dataIndex:'disabled', flex:1 }
               ],
               store:store
            }
         ]
      })

      this.callParent(arguments);
   }
});