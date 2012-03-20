Ext.define('practView.controller.Practitioner', {
   extend:'Ext.app.Controller',

   views:[
      'dragDropView',
      'practitioner.practGrid',
      'practitioner.practDetailedPanel',
      'practitioner.detailsViewPanel',
      'practitioner.defaultViewPanel',
      'gridView',
      'grid.nestedGrid',
      'grid.childGrid',
      'grid.userManagement'
   ],

   stores:[
      'Practitioner',
      'parentGridStore',
      'nestedGridStore',
      'User'
   ],
   models:['Practitioner'],

   managedPage:{},
   container:null,

   refs:[
      {
         ref:'practGrid',
         selector:'practGrid'
      },
      {
         ref:'practDetailedPanel',
         selector:'practDetailedPanel'
      },
      {
         ref:'detailsViewPanel',
         selector:'detailsViewPanel'
      },
      {
         ref:'defaultViewPanel',
         selector:'defaultViewPanel'
      },
      {
         ref:'nestedGrid',
         selector:'nestedGrid'
      },
      {
         ref:'userManagement',
         selector:'userManagement'
      }
   ],

   cboStore:null,

   init:function () {
      var me = this;

      this.registerPage('defaultView', 'practView.view.practitioner.defaultViewPanel');
      this.registerPage('detailsView', 'practView.view.practitioner.detailsViewPanel');
      this.registerPage('dragDropView', 'practView.view.dragDropView');
      this.registerPage('gridView', 'practView.view.gridView');
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
         'userManagement combo':{
            change:function (cbo, newVal, oldVal) {
               var gridStore = me.getUserManagement().down('grid').getStore();
               gridStore.clearFilter();
               if (newVal == null || newVal == '') {
               } else {
                  gridStore.clearFilter();
                  gridStore.filter([
                     {
                        filterFn:function (item) {
                           return item.get('email').search(newVal) > 1
                        }
                     }
                  ]);
               }
            }
         },
         'childGid dataview':{
            dblclick:function () {
               console.log('Received dbl click');
            },
            itemclick:function () {
               console.log('Received item click')
            }
         },
         'nestedGrid dataview':{
            expandbody:function (rowNode, record, expandRow, eOpts) {
               console.log('Expand node');
               var row = Ext.get(expandRow);
               if (row.childGrid == null || row.childGrid == undefined) {
                  // create child grid
                  var childGrid = Ext.create('practView.view.grid.childGrid', {
                     itemId:'childGrid-' + record.data.name,
                     id:'childGrid-' + record.data.name
                  });
                  console.debug(childGrid.getEl());

                  childGrid.on('render', function () {
                     // console.log('child grid rendered then swallow events');
                     // childGrid.getEl().swallowEvent(['mouseover', 'mousedown', 'click', 'dblclick', 'onRowFocus']);
                  });
                  row.childGrid = childGrid;
                  targetContainer = Ext.get(expandRow).down('.childGrid');
                  childGrid.render(targetContainer);

               }
            }
         },
         '#main-page':{
            render:function (eComp, eOpts) {
               console.debug('Dynamically added "defaultViewPanel"');
               me.container = Ext.getCmp('main-page');
               me.navigateToPage('gridView');
            }
         },
         'dragDropPanel':{
            render:function (eComp, eOpts) {
               console.debug('Panel : ' + eComp.id + ' was rendered');
            },

            afterrender:function () {
            },
            panelOnDrag:function (eComp, event) {
               console.debug('Controller received signal from ' + eComp.id + " that it is dragging...");
               console.debug('Firing event to signal others panel');
            },
            otherPanelOnDrag:function (eComp) {
               console.debug(eComp.id + " receiving signal that a panel has been dragging...")
            }
         },
         'practGrid':{
            beforerender:function () {
               this.getPractitionerStore().load({
                  callback:function (records, operation, success) {
                     // unmask
                     if (me.getPractGrid()) {
                        me.getPractGrid().hideLoading();
                     }
                  }});
            },
            render:function (eComp, eOpts) {
               console.debug('practGrid was rendered.');
               // show loading, hot fix for show loading
               eComp.showLoading();
            },
            itemclick:function (view, record, htmlItem, index, eventE, eOpts) {
               me.getPractDetailedPanel().updateDetail(record.data);
            },
            itemdblclick:function (view, record, htmlItem, index, e, eOpts) {
               me.navigateToPage('detailsView', function (pageComponent) {
                  pageComponent.fireEvent('updateDetail', record.data);
               });
            }
         },
         'detailsViewPanel':{
            render:function (eComp, eOpts) {
               console.debug('detailsViewPanel was rendered');
               eComp.processEventQueue();
            }
         },
         'detailsViewPanel #detailViewBackBtn':{
            click:function (button, event, eOpts) {
               console.debug('Button name: ' + button.id + ' clicked.')
               me.navigateToPage('defaultView');
            }
         }
      });
   },

   registerPage:function (pageName, viewClass) {
      this.managedPage[pageName] = viewClass;
   },
   navigateToPage:function (pageName, callback) {
      console.debug('Moving to page: ' + pageName);
      this.container.removeAll();
      var pageComponent = Ext.create(this.managedPage[pageName]);
      if (callback) {
         callback(pageComponent);
      }
      this.container.add(pageComponent);
      this.container.doLayout();
   },

   otherPanelOnDrag:function () {
      console.debug('Other panel on drag...');
   }

})
;