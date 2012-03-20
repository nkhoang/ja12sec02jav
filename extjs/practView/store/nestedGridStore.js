Ext.define('practView.store.nestedGridStore', {
   extend:'Ext.data.Store',
   fields:['name', 'senority', 'department'],
   data:[
      { "name":"Michael Scott", "senority":7, "department":"Manangement" },
      { "name":"Dwight Schrute", "senority":2, "department":"Sales" },
      { "name":"Jim Halpert", "senority":3, "department":"Sales" },
      { "name":"Kevin Malone", "senority":4, "department":"Accounting" },
      { "name":"Angela Martin", "senority":5, "department":"Accounting" }
   ],
   proxy:{
      type:'memory',
      reader:{
         type:'json',
         root:'employees'
      }
   }
})
;
