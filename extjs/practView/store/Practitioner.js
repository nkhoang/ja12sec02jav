Ext.define('practView.store.Practitioner', {
  extend:'Ext.data.Store',
  model:'practView.model.Practitioner',
  proxy:{
    type:'jsonp',
    url:'http://localhost:9090/services/demo/practitioner'
  }


})
// use this --allow-file-access-from-files