<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>

    <script type="text/javascript" src="<c:url value='/js/ext-all.js' />"></script>
    <link href="<c:url value='/resources/css/ext-all-gray.css'/>" rel="stylesheet" media="all"/>
    <style type="text/css">
        .dictionary-add {
            background-image: url('<c:url value="/styles/images/add.png" />') !important;
        }
        .dictionary-remove {
            background-image: url('<c:url value="/styles/images/delete.png" />') !important;
        }
    </style>
    <script type="text/javascript">
        var dictionaryStore;// define model for Dictionary
        $(function() {

            Ext.define('Dictionary', {
                extend: 'Ext.data.Model',
                fields: [
                    {name: 'id', type: 'int'},
                    'name',
                    'description'
                ]
            });

            dictionaryStore = Ext.create('Ext.data.Store', {
                model: 'Dictionary',
                proxy: new Ext.data.HttpProxy({
                    api: {
                        read: '<c:url value="/services/dictionary/getAll" />',
                        create: '<c:url value="/services/dictionary/saveDictionary" />',
                        update: '<c:url value="/services/dictionary/saveDictionary" />',
                        destroy: '<c:url value="/services/dictionary/deleteDictionary" />'
                    }
                }),
                reader: {
                    type: 'json',
                    root: 'data'
                },
                writer: {
                    type: 'json',
                    writeAllFields: false
                },
                autoLoad: true,
                autoSync: false,
                listeners: {
                    write: function(store, operation) {
                        var record = operation.records[0];
                        var name = Ext.String.capitalize(operation.action);
                        var verb;

                        if (name == 'Destroy') {
                            record = operation.records[0];
                            verb = 'Destroyed';
                        } else {
                            verb = name + 'd';
                        }
                        showMessage({
                             title : 'Dictionary',
                             text : Ext.String.format("{0} dictionary: {1}", verb, record.data.name)
                          });
                    }
                }
            });
            var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToMoveEditor: 1,
                    autoCancel: false,
                    listeners: {
                        'edit': function(editor) {
                            dictionaryStore.sync();
                            dictionaryStore.load();
                        }
                    }
                });
            var grid = Ext.create('Ext.grid.Panel', {
                id: 'dictionary-grid',
                store: dictionaryStore,
                columns: [
                    Ext.create('Ext.grid.RowNumberer'),
                    {
                        header: 'Name',
                        dataIndex: 'name',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: false
                        }
                    },
                    {
                        header: 'Description',
                        dataIndex: 'description',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: true
                        }
                    }
                ],
                renderTo: 'dict-grid-container',
                width: 600,
                height: 400,
                title: 'Available Dictionary',
                frame: true,
                tbar: [
                    {
                        text: 'Add Dictionary',
                        iconCls: 'dictionary-add',
                        handler : function() {
                            rowEditing.cancelEdit();
                            // Create a model instance
                            var r = Ext.create('Dictionary', {
                                name: '',
                                description: ''
                            });
                            dictionaryStore.insert(0, r);
                            rowEditing.startEdit(0, 0);
                        }
                    },
                    {
                        itemId: 'removeDictionary',
                        text: 'Remove Dictionary',
                        iconCls: 'dictionary-remove',
                        handler: function() {
                            var sm = grid.getSelectionModel();
                            rowEditing.cancelEdit();
                            dictionaryStore.remove(sm.getSelection());
                            dictionaryStore.sync();
                            if (dictionaryStore.getCount() > 0) {
                                sm.select(0);
                            }
                        },
                        disabled: true
                    }
                ],
                plugins: [rowEditing],
                listeners: {
                    'selectionchange': function(view, records) {
                        grid.down('#removeDictionary').setDisabled(!records.length);
                    }
                }
            });


            Ext.define('AppConfig', {
                extend: 'Ext.data.Model',
                fields: [
                    {name: 'id', type: 'int'},
                    'label',
                    'values'
                ]
            });

            appConfigStore = Ext.create('Ext.data.Store', {
                model: 'AppConfig',
                proxy: new Ext.data.HttpProxy({
                    api: {
                        read: '<c:url value="/services/appConfig/getAll" />',
                        create: '<c:url value="/services/appConfig/saveAppConfig" />',
                        update: '<c:url value="/services/appConfig/saveAppConfig" />',
                        destroy: '<c:url value="/services/appConfig/deleteAppConfig" />'
                    }
                }),
                reader: {
                    type: 'json',
                    root: 'data'
                },
                writer: {
                    type: 'json',
                    writeAllFields: false
                },
                autoLoad: true,
                autoSync: false,
                listeners: {
                    write: function(store, operation) {
                        var record = operation.records[0];
                        var name = Ext.String.capitalize(operation.action);
                        var verb;

                        if (name == 'Destroy') {
                            record = operation.records[0];
                            verb = 'Destroyed';
                        } else {
                            verb = name + 'd';
                        }
                        showMessage({
                             title : 'Dictionary',
                             text : Ext.String.format("{0} dictionary: {1}", verb, record.data.name)
                          });
                    }
                }
            });
            var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
                    clicksToMoveEditor: 1,
                    autoCancel: false,
                    listeners: {
                        'edit': function(editor) {
                            dictionaryStore.sync();
                            dictionaryStore.load();
                        }
                    }
                });
            var grid = Ext.create('Ext.grid.Panel', {
                id: 'dictionary-grid',
                store: dictionaryStore,
                columns: [
                    Ext.create('Ext.grid.RowNumberer'),
                    {
                        header: 'Name',
                        dataIndex: 'name',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: false
                        }
                    },
                    {
                        header: 'Description',
                        dataIndex: 'description',
                        flex: 1,
                        editor: {
                            // defaults to textfield if no xtype is supplied
                            allowBlank: true
                        }
                    }
                ],
                renderTo: 'dict-grid-container',
                width: 600,
                height: 400,
                title: 'Available Dictionary',
                frame: true,
                tbar: [
                    {
                        text: 'Add Dictionary',
                        iconCls: 'dictionary-add',
                        handler : function() {
                            rowEditing.cancelEdit();
                            // Create a model instance
                            var r = Ext.create('Dictionary', {
                                name: '',
                                description: ''
                            });
                            dictionaryStore.insert(0, r);
                            rowEditing.startEdit(0, 0);
                        }
                    },
                    {
                        itemId: 'removeDictionary',
                        text: 'Remove Dictionary',
                        iconCls: 'dictionary-remove',
                        handler: function() {
                            var sm = grid.getSelectionModel();
                            rowEditing.cancelEdit();
                            dictionaryStore.remove(sm.getSelection());
                            dictionaryStore.sync();
                            if (dictionaryStore.getCount() > 0) {
                                sm.select(0);
                            }
                        },
                        disabled: true
                    }
                ],
                plugins: [rowEditing],
                listeners: {
                    'selectionchange': function(view, records) {
                        grid.down('#removeDictionary').setDisabled(!records.length);
                    }
                }
            });
        });
    </script>
</head>
<body>
<div id="dict-grid-container"></div>
<%@ include file="/common/notify-template.jsp" %>
</body>
</html>