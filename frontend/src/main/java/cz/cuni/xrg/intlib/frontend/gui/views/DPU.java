package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Validator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.dpu.TemplateConfiguration;
import cz.cuni.xrg.intlib.commons.app.dpu.VisibilityType;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ModuleDialogGetter;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;

import java.util.List;

/**
 * @author Maria Kukhar 
 */

class DPU extends ViewComponent {

	/*
	 * @AutoGenerated private AbsoluteLayout mainLayout;
	 * 
	 * @AutoGenerated private Label label; private Label lblUri; private
	 * TextField txtUri; private Button btnOpenDialog;
	 */
	private VerticalLayout mainLayout;
	private VerticalLayout verticalLayoutData;
	private VerticalLayout verticalLayoutConfigure;
	private VerticalLayout dpuDetailLayout;
	private Tree dpuTree;
	private TextField dpuName;
	private TextArea dpuDescription;
//	private OptionGroup groupType;
	private TabSheet tabSheet;
	private cz.cuni.xrg.intlib.commons.app.dpu.DPU selectedDpu;
	private OptionGroup groupVisibility;
	private DPUExecutive dpuExec;
	String jarPath;
	private GridLayout dpuLayout;
	private HorizontalLayout buttonDpuBar;
	private Configuration conf;
	
	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public DPU() {

	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setStyleName("mainLayout");

		// top-level component properties
		setSizeUndefined();

		/*
		 * // label label = new Label(); label.setImmediate(false);
		 * label.setWidth("-1px"); label.setHeight("-1px");
		 * label.setValue("<h1>DPUs</h>");
		 * label.setContentMode(ContentMode.HTML);
		 * mainLayout.addComponent(label, "top:30.0px;left:80.0px;");
		 * 
		 * lblUri = new Label(); lblUri.setValue("uri:");
		 * mainLayout.addComponent(lblUri, "top:100.0px;left:80.0px;");
		 * 
		 * txtUri = new TextField(); txtUri.setWidth("480px");
		 * txtUri.setHeight("25px"); mainLayout.addComponent(txtUri,
		 * "top:120.0px;left:80.0px;");
		 * 
		 * btnOpenDialog = new Button();
		 * btnOpenDialog.setCaption("show dialog");
		 * btnOpenDialog.setWidth("100px"); btnOpenDialog.setHeight("25px");
		 * btnOpenDialog.addClickListener(new ClickListener() {
		 * 
		 * @Override public void buttonClick(ClickEvent event) {
		 * App.getApp().getNavigator().navigateTo( "expDialog/" +
		 * txtUri.getValue() ); } }); mainLayout.addComponent(btnOpenDialog,
		 * "top:170.0px;left:200.0px;");
		 */

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidth("100%");

		Button buttonCreateDPU = new Button();
		buttonCreateDPU.setCaption("Create DPU");
		buttonCreateDPU.setHeight("25px");
		buttonCreateDPU.setWidth("100px");
		buttonCreateDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
					}
				});
		buttonBar.addComponent(buttonCreateDPU);

		Button buttonImportDPU = new Button();
		buttonImportDPU.setCaption("Import DPU");
		buttonImportDPU.setHeight("25px");
		buttonImportDPU.setWidth("100px");
		buttonImportDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

					}
				});
		buttonBar.addComponent(buttonImportDPU);

		Button buttonExportAll = new Button();
		buttonExportAll.setCaption("Export All");
		buttonExportAll.setHeight("25px");
		buttonExportAll.setWidth("100px");
		buttonExportAll
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

					}
				});
		buttonBar.addComponent(buttonExportAll);
		mainLayout.addComponent(buttonBar);

		GridLayout dpuLayout = buildDpuLayout();
		mainLayout.addComponent(dpuLayout);

		return mainLayout;
	}

	@SuppressWarnings({ "serial", "deprecation" })
	private GridLayout buildDpuLayout() {
		// TODO Auto-generated method stub
		dpuLayout = new GridLayout(2, 2);
		dpuLayout.setSpacing(true);
		dpuLayout.setWidth("100%");
		dpuLayout.setHeight("100%");
		dpuLayout.setRowExpandRatio(0, 0.01f);
		dpuLayout.setRowExpandRatio(1, 0.99f);

		HorizontalLayout filterBar = new HorizontalLayout();
		filterBar.setSpacing(true);

		CheckBox onlyMyDPU = new CheckBox();
		onlyMyDPU.setCaption("Only My DPU");
		filterBar.addComponent(onlyMyDPU);

		Label labelFilter = new Label();
		labelFilter.setContentMode(ContentMode.HTML);
		labelFilter.setValue("<span style=padding-left:28px>Filter:</span>");
		filterBar.addComponent(labelFilter);

		TextField treeFilter = new TextField();
		treeFilter.setImmediate(false);
		treeFilter.setInputPrompt("Type to filter tree");
		treeFilter.addListener(new FieldEvents.TextChangeListener() {

			SimpleTreeFilter filter = null;

			@Override
			public void textChange(TextChangeEvent event) {
				// TODO Auto-generated method stub
				Filterable f = (Filterable) dpuTree.getContainerDataSource();

				// Remove old filter
				if (filter != null)
					f.removeContainerFilter(filter);

				// Set new filter 
				filter = new SimpleTreeFilter( event.getText(), true, false);
				f.addContainerFilter(filter);

			}
		});


		filterBar.addComponent(treeFilter);

		dpuLayout.addComponent(filterBar, 0, 0);

		dpuTree = new Tree("DPUs");
		dpuTree.setImmediate(true);
		dpuTree.setHeight(630, Unit.PIXELS);

		dpuTree.setStyleName("dpuTree");
		fillTree(dpuTree);
		for (Object itemId: dpuTree.rootItemIds())
			dpuTree.expandItemsRecursively(itemId);
		dpuTree.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				selectedDpu = (cz.cuni.xrg.intlib.commons.app.dpu.DPU) event
						.getItemId();
				jarPath = selectedDpu.getJarPath();

				
				if ((selectedDpu != null) && (selectedDpu.getId() != null)) {
					
					dpuLayout.removeComponent(dpuDetailLayout);
					dpuDetailLayout = buildDPUDetailLayout();
					dpuLayout.addComponent(dpuDetailLayout, 1, 0, 1, 1);

					String selectedDpuName = selectedDpu.getName();
					String selecteDpuDescription = selectedDpu.getDescription();
					VisibilityType selecteDpuVisibility = selectedDpu.getVisibility();
					dpuName.setValue(selectedDpuName);
					dpuDescription.setValue(selecteDpuDescription);
					if (selecteDpuVisibility == VisibilityType.PRIVATE){
						groupVisibility.setValue(selecteDpuVisibility);
						groupVisibility.setEnabled(false);
					}
					else{ 
						groupVisibility.setValue(selecteDpuVisibility);
						groupVisibility.setEnabled(true);
					}

				}

				else {
					dpuLayout.removeComponent(dpuDetailLayout);
					
				}
			}
		});
		
			
		dpuLayout.addComponent(dpuTree, 0, 1);

		return dpuLayout;
	}
	
	private VerticalLayout buildDPUDetailLayout() {
		
		dpuDetailLayout = new VerticalLayout();
		dpuDetailLayout.setImmediate(false);
		dpuDetailLayout.setWidth("100.0%");
		dpuDetailLayout.setHeight("100%");
		dpuDetailLayout.setMargin(true);
		
		
		tabSheet = new TabSheet();
		verticalLayoutData = buildVerticalLayoutData();
		Tab dataTab = tabSheet.addTab(verticalLayoutData, "Data");

		verticalLayoutConfigure = new VerticalLayout();
		verticalLayoutConfigure.setImmediate(false);
		verticalLayoutConfigure.setWidth("100.0%");
		verticalLayoutConfigure.setHeight("100%");
		verticalLayoutConfigure.setMargin(true);
		tabSheet.addTab(verticalLayoutConfigure, "Configure");
		tabSheet.setSelectedTab(dataTab);

		tabSheet.setWidth(600, Unit.PIXELS);
		tabSheet.setHeight(400, Unit.PIXELS);
		
		
		dpuDetailLayout.addComponent(tabSheet);
		
		if (jarPath !=null){
			try {
				dpuExec = App.getApp().getModules().getInstance(jarPath);
				
				// get configuration from dpu
				conf = selectedDpu.getTemplateConfiguration();				
		
				if (dpuExec != null) {
					
					if (conf == null) {
						// create new default configuration
						conf = new TemplateConfiguration();
						dpuExec.saveConfigurationDefault(conf);
					}	
					
					CustomComponent dpuConfigurationDialog = ModuleDialogGetter.getDialog(dpuExec, conf);
					dpuConfigurationDialog.setWidth("100%");
					verticalLayoutConfigure.removeAllComponents();
					verticalLayoutConfigure.addComponent(dpuConfigurationDialog);
					
				}
				
			} catch (ModuleException me) {
				//TODO: Show info about failed load of custom part of dialog
				Notification.show("ModuleException:Failed to load configuration dialog.", me.getTraceMessage(), Type.ERROR_MESSAGE);
			} catch (ConfigurationException ce) {
				//TODO: Show info about invalid saved config(should not happen -> validity check on save)
				Notification.show("ConfigurationException: Failed to set configuration for dialog.",
						ce.getMessage(), Type.ERROR_MESSAGE);
				} 
			}
		
		buttonDpuBar = buildDPUButtonBur();
		dpuDetailLayout.addComponent(buttonDpuBar);

return dpuDetailLayout;
}	

	private VerticalLayout buildVerticalLayoutData() {

		// common part: create layout
		verticalLayoutData = new VerticalLayout();
		verticalLayoutData.setImmediate(false);
		verticalLayoutData.setWidth("100.0%");
		verticalLayoutData.setHeight("100%");
		verticalLayoutData.setMargin(true);

		GridLayout dpuSettingsLayout = new GridLayout(2, 4);
		dpuSettingsLayout.setStyleName("dpuSettingsLayout");
		dpuSettingsLayout.setMargin(true);
		dpuSettingsLayout.setSpacing(true);
		dpuSettingsLayout.setWidth("100%");
		dpuSettingsLayout.setColumnExpandRatio(0, 0.10f);
		dpuSettingsLayout.setColumnExpandRatio(1, 0.90f);

		Label nameLabel = new Label("Name:");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuSettingsLayout.addComponent(nameLabel, 0, 0);
		dpuName = new TextField();
		dpuName.setImmediate(true);
		dpuName.setWidth("200px");
		dpuName.setHeight("-1px");
		dpuName.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				throw new InvalidValueException("Name must be filled!");
			}
		});
		dpuSettingsLayout.addComponent(dpuName, 1, 0);
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuSettingsLayout.addComponent(descriptionLabel, 0, 1);
		dpuDescription = new TextArea();
		dpuDescription.setImmediate(true);
		dpuDescription.setWidth("100%");
		dpuDescription.setHeight("60px");
		dpuSettingsLayout.addComponent(dpuDescription, 1, 1);

		Label visibilityLabel = new Label("Visibility:");
		dpuSettingsLayout.addComponent(visibilityLabel, 0, 2);

		groupVisibility = new OptionGroup();
		groupVisibility.addStyleName("horizontalgroup");
		groupVisibility.addItem(VisibilityType.PRIVATE);
		groupVisibility.addItem(VisibilityType.PUBLIC);

		dpuSettingsLayout.addComponent(groupVisibility, 1, 2);

		verticalLayoutData.addComponent(dpuSettingsLayout); 


		return verticalLayoutData;
	}
	
	private HorizontalLayout buildDPUButtonBur() {
		
		buttonDpuBar = new HorizontalLayout();
		buttonDpuBar.setWidth(600, Unit.PIXELS);
		buttonDpuBar.setHeight("100%");
		buttonDpuBar.setSpacing(false);

		Button buttonCopyDPU = new Button();
		buttonCopyDPU.setCaption("Copy DPU");
		buttonCopyDPU.setHeight("25px");
		buttonCopyDPU.setWidth("100px");
		buttonCopyDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
					}
				});
		buttonDpuBar.addComponent(buttonCopyDPU);
		buttonDpuBar.setExpandRatio(buttonCopyDPU, 0.85f);
		buttonDpuBar.setComponentAlignment(buttonCopyDPU, Alignment.BOTTOM_LEFT);

		Button buttonDeleteDPU = new Button();
		buttonDeleteDPU.setCaption("Delete DPU");
		buttonDeleteDPU.setHeight("25px");
		buttonDeleteDPU.setWidth("100px");
		buttonDeleteDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						
						List<DPUInstance> instances = App.getDPUs().getAllDPUInstances();
						int fl=0;
						for (DPUInstance item : instances){
							
							if ((item.getDpu().getId()) == (selectedDpu.getId())) fl=1;
							
						}
						if(fl==0){
							
							App.getApp().getDPUs().delete(selectedDpu);
							dpuTree.removeAllItems();
							fillTree(dpuTree);
							dpuDetailLayout.removeAllComponents();
							Notification.show("DPU was removed", Notification.Type.HUMANIZED_MESSAGE);
						}
						else Notification.show("DPU can not be removed because it has been used in Pipeline", Notification.Type.WARNING_MESSAGE);
													
															
					}
				});
		buttonDpuBar.addComponent(buttonDeleteDPU);
		buttonDpuBar.setExpandRatio(buttonDeleteDPU, 0.85f);
		buttonDpuBar.setComponentAlignment(buttonDeleteDPU,		Alignment.BOTTOM_LEFT);

		Button buttonExportDPU = new Button();
		buttonExportDPU.setCaption("Export DPU");
		buttonExportDPU.setHeight("25px");
		buttonExportDPU.setWidth("100px");
		buttonExportDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

					}
				});
		buttonDpuBar.addComponent(buttonExportDPU);
		buttonDpuBar.setExpandRatio(buttonExportDPU, 2.55f);
		buttonDpuBar.setComponentAlignment(buttonExportDPU,		Alignment.BOTTOM_LEFT);

		Button buttonSaveDPU = new Button();
		buttonSaveDPU.setCaption("Save");
		buttonSaveDPU.setHeight("25px");
		buttonSaveDPU.setWidth("100px");
		buttonSaveDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {
						if ((selectedDpu != null) && (selectedDpu.getId() != null)) {
							selectedDpu.setName(dpuName.getValue());
							selectedDpu.setDescription(dpuDescription
									.getValue());
							selectedDpu.setVisibility((VisibilityType)groupVisibility
									.getValue());
							
							dpuExec.saveConfiguration(conf);
														
							// store into DB
							App.getDPUs().save(selectedDpu);
							

						}

					}
				});
		buttonDpuBar.addComponent(buttonSaveDPU);
		buttonDpuBar.setComponentAlignment(buttonSaveDPU,
				Alignment.BOTTOM_RIGHT);
		dpuDetailLayout.addComponent(buttonDpuBar);

		
		return buttonDpuBar;
	}

	private void fillTree(Tree tree) {

		cz.cuni.xrg.intlib.commons.app.dpu.DPU rootExtractor = new cz.cuni.xrg.intlib.commons.app.dpu.DPU(
				"Extractors", null);
		tree.addItem(rootExtractor);
		cz.cuni.xrg.intlib.commons.app.dpu.DPU rootTransformer = new cz.cuni.xrg.intlib.commons.app.dpu.DPU(
				"Transformers", null);
		tree.addItem(rootTransformer);
		cz.cuni.xrg.intlib.commons.app.dpu.DPU rootLoader = new cz.cuni.xrg.intlib.commons.app.dpu.DPU(
				"Loaders", null);
		tree.addItem(rootLoader);

		List<cz.cuni.xrg.intlib.commons.app.dpu.DPU> dpus = App.getApp()
				.getDPUs().getAllDpus();
		for (cz.cuni.xrg.intlib.commons.app.dpu.DPU dpu : dpus) {
			tree.addItem(dpu);

			switch (dpu.getType()) {
			case EXTRACTOR:
				tree.setParent(dpu, rootExtractor);
				break;
			case TRANSFORMER:
				tree.setParent(dpu, rootTransformer);
				break;
			case LOADER:
				tree.setParent(dpu, rootLoader);
				break;
			default:
				throw new IllegalArgumentException();
			}
		}

	}


	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

}
