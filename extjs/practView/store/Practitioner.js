Ext.define('practView.store.Practitioner', {
  extend:'Ext.data.Store',
  model:'practView.model.Practitioner',
  proxy:{
    type:'ajax',
    url:'practView/data/practitioner.json'
  }
})