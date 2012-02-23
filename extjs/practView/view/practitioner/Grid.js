Ext.define('practView.view.practitioner.Grid', {
  extend:'Ext.grid.Panel',
  alias:'widget.practitionerGrid',

  initComponent:function () {
    this.store = 'Practitioner';
    this.columns = [
      {
        text:'Name',
        width:200,
        dataIndex:'name',
        sortable:true
      },
      {
        text:'NPI',
        width:120,
        dataIndex:'npi',
        sortable:true
      },
      {
        text:'Type',
        width:200,
        dataIndex:'type',
        sortable:true
      },
      {
        text:'Address',
        width:250,
        dataIndex:'address',
        sortable:true
      },
      {
        text:'Status',
        width:50,
        dataIndex:'status',
        sortable:true
      },
      {
        text:'License State',
        width:50,
        dataIndex:'licenseState',
        sortable:true
      },
      {
        text:'Vendible',
        width:40,
        dataIndex:'vendible',
        sortable:true
      },
      {
        text:'Number of Affiliations',
        width:40,
        dataIndex:'numAffil',
        sortable:true
      }
    ];

    this.viewConfig = {
      forceFit: true
    }

    this.callParent(arguments);
  }
});