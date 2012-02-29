Ext.define('practView.controller.Practitioner', {
  extend: 'Ext.app.Controller',

  views: [
    'practitioner.practGrid',
    'practitioner.practDetailedPanel',
    'practitioner.detailsViewPanel',
    'practitioner.defaultViewPanel'
  ],

  stores: ['Practitioner'],
  models: ['Practitioner'],

  managedPage: {},
  container  : null,

  refs: [
    {
      ref     : 'practGrid',
      selector: 'practGrid'
    },
    {
      ref     : 'practDetailedPanel',
      selector: 'practDetailedPanel'
    },
    {
      ref     : 'detailsViewPanel',
      selector: 'detailsViewPanel'
    },
    {
      ref     : 'defaultViewPanel',
      selector: 'defaultViewPanel'
    }
  ],

  init: function () {
    var me = this;

    this.registerPage('defaultView', 'practView.view.practitioner.defaultViewPanel');
    this.registerPage('detailsView', 'practView.view.practitioner.detailsViewPanel');
    /*
     me.eventManager = Ext.create('practView.event.EventManager');
     // init event manager.
     me.eventManager.addEvents('selectGridRow', 'gridLoad', 'gridRendered');

     // create event handler
     me.eventManager.on({
     'gridRendered': {
     scope: this,
     fn   : function () {
     this.getPractitionerStore().load({
     callback: function (records, operation, success) {
     console.log('Practitioner Store loaded.');
     if (me.getPractGrid()) {
     me.getPractGrid().hideLoading();
     }
     }
     });
     }
     }
     }
     );*/

    var me = this;
    this.control({
      '#main-page'                         : {
        render: function (eComp, eOpts) {
          console.debug('Dynamically added "defaultViewPanel"');
          me.container = Ext.getCmp('main-page');
          me.navigateToPage('defaultView');
        }
      },
      'practGrid'                          : {
        beforerender: function () {
          this.getPractitionerStore().load({
            callback: function (records, operation, success) {
              // unmask
              if (me.getPractGrid()) {
                me.getPractGrid().hideLoading();
              }
            }});
        },
        render      : function (eComp, eOpts) {
          console.debug('practGrid was rendered.');
          // show loading, hot fix for show loading
          eComp.showLoading();
        },
        itemclick   : function (view, record, htmlItem, index, eventE, eOpts) {
          me.getPractDetailedPanel().updateDetail(record.data);
        },
        itemdblclick: function (view, record, htmlItem, index, e, eOpts) {
          me.navigateToPage('detailsView', function (pageComponent) {
            pageComponent.fireEvent('updateDetail', record.data);
          });
        }
      },
      'detailsViewPanel'                   : {
        render: function (eComp, eOpts) {
          console.debug('detailsViewPanel was rendered');
          eComp.processEventQueue();
        }
      },
      'detailsViewPanel #detailViewBackBtn': {
        click: function (button, event, eOpts) {
          console.debug('Button name: ' + button.id + ' clicked.')
          me.navigateToPage('defaultView');
        }
      }
    });
  },

  registerPage  : function (pageName, viewClass) {
    this.managedPage[pageName] = viewClass;
  },
  navigateToPage: function (pageName, callback) {
    console.debug('Moving to page: ' + pageName);
    this.container.removeAll();
    var pageComponent = Ext.create(this.managedPage[pageName]);
    if (callback) {
      callback(pageComponent);
    }
    this.container.add(pageComponent);
    this.container.doLayout();
  }
})
;