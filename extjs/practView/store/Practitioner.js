Ext.define('practView.store.Practitioner', {
  extend:'Ext.data.Store',
  model:'practView.model.Practitioner',
  proxy:{
    type:'rest',
    url:'http://localhost:9090/services/demo/practitioner',
    reader: {
      type: 'json'
    }
  }


})
// use this --allow-file-access-from-files