Ext.define('practView.model.Practitioner', {
  extend:'Ext.data.Model',
  fields:[
    "name", "npi", "type", "address", "status", "licenseState", "vendible", "numAffil"
  ]
});