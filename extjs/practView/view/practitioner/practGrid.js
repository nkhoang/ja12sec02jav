Ext.define('practView.view.practitioner.practGrid', {
  extend:'Ext.grid.Panel',
  alias:'widget.practGrid',
  itemId:'practGrid',
  store:'Practitioner',

  initComponent:function () {
    var me = this;

    this.showLoading = function () {
      setTimeout(function () {
        me.getEl().mask('Loading...')
      }, 100);
    };

    this.hideLoading = function () {
      this.getEl().unmask();
    };
    this.columns = [
      {
        text:'Name',
        width:150,
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
        width:150,
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
        width:60,
        dataIndex:'status',
        sortable:true
      },
      {
        text:'License State',
        width:70,
        dataIndex:'licenseState',
        sortable:true
      },
      {
        text:'Vendible',
        width:60,
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
      forceFit:true
    }
    this.callParent(arguments);
  }
});