Ext.define('practView.store.User', {
   extend:'Ext.data.Store',
   fields:['email', 'firstName', 'lastName', 'lastLogin', 'disabled'],
   autoLoad: false,
   data:{'items':[
      { 'firstName':'User', 'lastName':'1', "email":"user1@healthmarketscience.com", "lastLogin":"1/1/2012 10:10:11" , 'disabled': true },
      { 'firstName':'User', 'lastName':'2', "email":"user2@healthmarketscience.com", "lastLogin":"1/2/2012 10:20:11", 'disabled': false  },
      { 'firstName':'User', 'lastName':'3', "email":"user3@healthmarketscience.com", "lastLogin":"1/3/2012 10:30:11", 'disabled': false  },
      { 'firstName':'User', 'lastName':'4', "email":"user4@bayer.com", "lastLogin":"1/4/2012 10:00:11", disabled: false  }
   ]},
   proxy:{
      type:'memory',
      reader:{
         type:'json',
         root:'items'
      }
   }
});