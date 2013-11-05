package cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.mff.xrg.odcs.commons.app.auth.VisibilityType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUTemplateWrap;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu.DPUWrapException;
import cz.cuni.mff.xrg.odcs.frontend.gui.AuthAwareUploadSucceededWrapper;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DPUTree;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.FileUploadReceiver;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.UploadInfoWindow;
import cz.cuni.mff.xrg.odcs.frontend.gui.tables.IntlibPagedTable;
import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author Bogo
 */
public class DPUViewImpl extends CustomComponent implements DPUView {

	private DPUViewListener listener;
	private static final long serialVersionUID = 1L;
	private VerticalLayout mainLayout;
	private VerticalLayout verticalLayoutData; //Layout contains General tab components of {@link #tabSheet}.
	private VerticalLayout verticalLayoutConfigure;// Layout contains Template Configuration tab components of {@link #tabSheet}. 
	private VerticalLayout verticalLayoutInstances;//Layout contains DPU instances tab components of {@link #tabSheet}.  
	private VerticalLayout dpuDetailLayout; //Layout contains DPU Template details.
	private DPUTree dpuTree;// Tree contains available DPUs.
	private TextField dpuName; // name of selected DPU Template
	private TextArea dpuDescription; // description of selected DPU Template
	private Upload reloadFile; // button for reload JAR file
	private FileUploadReceiver fileUploadReceiver;
	public static UploadInfoWindow uploadInfoWindow;
	private boolean errorExtension = false;
	private Label jarPath;
	/**
	 * DPU Template details TabSheet contains General, Template Configuration,
	 * DPU instances tabs
	 */
	private TabSheet tabSheet;
	private OptionGroup groupVisibility; // Visibility of DPU Template: public or private
	private GridLayout dpuLayout; // Layout contains DPU Templates tree and DPU Template details.
	private HorizontalLayout buttonDpuBar; // Layout contains action buttons of DPU Template details.
	private HorizontalLayout layoutInfo; // Layout with the information that no DPU template was selected.
	/**
	 * Table with instances of DPU. Located on {@link #tabSheet} DPU instances
	 * tab.
	 */
	private IntlibPagedTable instancesTable;
	private IndexedContainer tableData; //container with instancesTable data
	/**
	 * Wrap for selected DPUTemplateRecord.
	 */
	private DPUTemplateWrap selectedDpuWrap = null;
	private static final Logger LOG = LoggerFactory.getLogger(ViewComponent.class);
	private Button buttonSaveDPU;
	private String tabname;
	
	public DPUViewImpl() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Override
	public void setListener(DPUViewListener listener) {
		this.listener = listener;
	}

	/**
	 * Layout contains DPU Templates page elements: buttons on the top: "Create
	 * DPU", "Import DPU", "Export All"; layout with DPU Templates tree
	 * {@link DPUTree} and DPU Template details
	 *
	 * @return mainLayout VerticalLayout with all components of DPU Templates
	 * page.
	 */
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
		setWidth("100%");
		setHeight("100%");

		// Buttons on the top: "Create DPU", "Import DPU", "Export All"
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setSpacing(true);

		Button buttonCreateDPU = new Button();
		buttonCreateDPU.setCaption("Create DPU template");
		buttonCreateDPU.setHeight("25px");
		buttonCreateDPU.setWidth("150px");
		buttonCreateDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				listener.event("openDPUCreate");
			}
		});
		buttonBar.addComponent(buttonCreateDPU);


		Button buttonImportDPU = new Button();
		buttonImportDPU.setCaption("Import DPU template");
		buttonImportDPU.setHeight("25px");
		buttonImportDPU.setWidth("150px");
		buttonImportDPU.setEnabled(false);
		buttonImportDPU
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				listener.event("importDPUTemplate");
			}
		});
		buttonBar.addComponent(buttonImportDPU);

		Button buttonExportAll = new Button();
		buttonExportAll.setCaption("Export All");
		buttonExportAll.setHeight("25px");
		buttonExportAll.setWidth("150px");
		buttonExportAll.setEnabled(false);
		buttonExportAll
				.addClickListener(new com.vaadin.ui.Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				listener.event("exportAll");
			}
		});
		buttonBar.addComponent(buttonExportAll);

		mainLayout.addComponent(buttonBar);

		//layout with  DPURecord tree and DPURecord details 
		dpuLayout = buildDpuLayout();
		mainLayout.addComponent(dpuLayout);
		return mainLayout;
	}

	/**
	 * Builds layout contains DPU Templates tree {@link DPUTree} and DPU
	 * Template details. Calls from {@link #buildMainLayout}
	 *
	 * @return dpuLayout GridLayout contains {@link DPUTree} and
	 * {@link #buildDPUDetailLayout}.
	 */
	private GridLayout buildDpuLayout() {

		dpuLayout = new GridLayout(3, 1);
		dpuLayout.setSpacing(true);
		dpuLayout.setHeight(630, Unit.PIXELS);
		dpuLayout.setRowExpandRatio(0, 0.01f);
		dpuLayout.setRowExpandRatio(1, 0.99f);

		// Layout with the information that no DPU template was selected.
		layoutInfo = new HorizontalLayout();
		layoutInfo.setHeight("100%");
		layoutInfo.setWidth("100%");
		Label infoLabel = new Label();
		infoLabel.setImmediate(false);
		infoLabel.setWidth("-1px");
		infoLabel.setHeight("-1px");
		infoLabel.setValue("<br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Select DPU template from the DPU template tree for displaying it's details.");
		infoLabel.setContentMode(ContentMode.HTML);
		layoutInfo.addComponent(infoLabel);

		//DPU Template Tree
		dpuTree = new DPUTree(false);
		dpuTree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(final ItemClickEvent event) {
				listener.selectDPU(event.getItemId().getClass() == DPUTemplateRecord.class ? (DPUTemplateRecord) event.getItemId() : null);
			}
		});

		dpuLayout.addComponent(dpuTree, 0, 0);
		dpuLayout.addComponent(layoutInfo, 2, 0);

		return dpuLayout;
	}

	/**
	 * Builds layout with DPU Template details of DPU selected in the tree. DPU
	 * Template details represents by {@link #tabSheet}. Calls from
	 * {@link #buildDpuLayout}
	 *
	 * @return dpuDetailLayout VerticalLayout with {@link #tabSheet} that
	 * contain all DPU Template details components.
	 */
	private VerticalLayout buildDPUDetailLayout() {

		dpuDetailLayout = new VerticalLayout();
		dpuDetailLayout.setImmediate(true);
		dpuDetailLayout.setStyleName("dpuDetailLayout");
		dpuDetailLayout.setMargin(true);

		//DPU Details TabSheet
		tabSheet = new TabSheet();
		tabSheet.setWidth(630, Unit.PIXELS);
		tabSheet.setHeight(350, Unit.PIXELS);
		tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {

				if (buttonSaveDPU != null) {
					tabname = event.getTabSheet().getSelectedTab().getCaption();
					if (isChanged() || tabname.equals("configuration")) {
						buttonSaveDPU.setEnabled(listener.hasPermission("save"));
					} else {
						buttonSaveDPU.setEnabled(false);
					}
				}
			}
		});


		//General tab. Contains informations: name, description, visibility,
		//information about JAR file.
		verticalLayoutData = buildVerticalLayoutData();

		verticalLayoutData.setCaption(
				"general");
		TabSheet.Tab dataTab = tabSheet.addTab(verticalLayoutData, "General");
		//Template Configuration tab. Contains information about configuration 
		//from JAR file
		verticalLayoutConfigure = new VerticalLayout();

		verticalLayoutConfigure.setImmediate(
				false);
		verticalLayoutConfigure.setMargin(
				true);
		verticalLayoutConfigure.setCaption(
				"configuration");
		tabSheet.addTab(verticalLayoutConfigure,
				"Template Configuration");
		tabSheet.setSelectedTab(dataTab);
		if (selectedDpuWrap
				!= null) {
			AbstractConfigDialog<DPUConfigObject> configDialog = null;
			//getting configuration dialog of selected DPU Template
			try {
				configDialog = selectedDpuWrap.getDialog();
			} catch (ModuleException ex) {
				Notification.show(
						"Failed to load configuration dialog",
						ex.getMessage(), Notification.Type.ERROR_MESSAGE);
				LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), ex);
			} catch (FileNotFoundException ex) {
				Notification.show(
						"File not found",
						ex.getMessage(), Notification.Type.ERROR_MESSAGE);
				LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), ex);
			} catch (Exception ex) {
				Notification.show("Failed to load configuration dialog", ex.getMessage(), Notification.Type.ERROR_MESSAGE);
				LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), ex);
			}

			verticalLayoutConfigure.removeAllComponents();
			if (configDialog == null) {
				// use some .. dummy component
			} else {
				// configure
				configureDPUDialog();
				verticalLayoutConfigure.addComponent(configDialog);
			}
		}
		//DPU instances tab. Contains pipelines using the given DPU. 
		verticalLayoutInstances = buildVerticalLayoutInstances();

		verticalLayoutInstances.setCaption(
				"instances");
		tabSheet.addTab(verticalLayoutInstances,
				"DPU instances");

		dpuDetailLayout.addComponent(tabSheet);
		buttonDpuBar = buildDPUButtonBar();

		dpuDetailLayout.addComponent(buttonDpuBar);
		return dpuDetailLayout;
	}

	/**
	 * Building layout contains action buttons of DPU Template details. Copy,
	 * Delete, Export, Save.
	 *
	 * @return buttonDpuBar HorizontalLayout contains action buttons.
	 */
	private HorizontalLayout buildDPUButtonBar() {

		buttonDpuBar = new HorizontalLayout();
		buttonDpuBar.setWidth("100%");
		buttonDpuBar.setHeight(30, Unit.PIXELS);
		buttonDpuBar.setSpacing(false);

		final DPUTemplateRecord selectedDpu = selectedDpuWrap.getDPUTemplateRecord();

		// Copy DPU Template Button, may copy only DPU of 3 level.
		Button buttonCopyDPU = new Button();
		buttonCopyDPU.setCaption("Copy");
		buttonCopyDPU.setHeight("25px");
		buttonCopyDPU.setWidth("100px");
		if (selectedDpu.getParent() != null) {
			// check permissions .. 
			buttonCopyDPU.setEnabled(listener.hasPermission("copy"));
		} else {
			buttonCopyDPU.setEnabled(false);
		}
		buttonCopyDPU.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				listener.event("copyDPU");

				// refresh data in dpu tree
				dpuTree.refresh();

			}
		});
		buttonDpuBar.addComponent(buttonCopyDPU);
		buttonDpuBar.setExpandRatio(buttonCopyDPU, 0.85f);
		buttonDpuBar
				.setComponentAlignment(buttonCopyDPU, Alignment.BOTTOM_LEFT);

		// Delete DPU Template Button
		Button buttonDeleteDPU = new Button();
		buttonDeleteDPU.setCaption("Delete");
		buttonDeleteDPU.setHeight("25px");
		buttonDeleteDPU.setWidth("100px");
		buttonDeleteDPU.setEnabled(listener.hasPermission("delete"));
		buttonDeleteDPU.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				//open confirmation dialog
				ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting DPU template",
						"Delete " + selectedDpu.getName().toString() + " DPU template?", "Delete", "Cancel",
						new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClose(ConfirmDialog cd) {
						if (cd.isConfirmed()) {
							listener.event("deleteDPU");
						}
					}
				});

			}
		});
		buttonDpuBar.addComponent(buttonDeleteDPU);
		buttonDpuBar.setExpandRatio(buttonDeleteDPU, 0.85f);
		buttonDpuBar.setComponentAlignment(buttonDeleteDPU,
				Alignment.BOTTOM_LEFT);

		// Export DPU Template Button
		Button buttonExportDPU = new Button();
		buttonExportDPU.setCaption("Export");
		buttonExportDPU.setHeight("25px");
		buttonExportDPU.setWidth("100px");
		buttonExportDPU.setEnabled(listener.hasPermission("export"));
		buttonExportDPU.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
			}
		});
		buttonDpuBar.addComponent(buttonExportDPU);
		buttonDpuBar.setExpandRatio(buttonExportDPU, 2.55f);
		buttonDpuBar.setComponentAlignment(buttonExportDPU,
				Alignment.BOTTOM_LEFT);

		// Save DPU Template Button
		buttonSaveDPU = new Button();
		buttonSaveDPU.setCaption("Save");
		buttonSaveDPU.setHeight("25px");
		buttonSaveDPU.setWidth("100px");
		buttonSaveDPU.setEnabled(false);
		buttonSaveDPU.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				saveDPUTemplate();
				//refresh data in dialog and dpu tree
				dpuTree.refresh();
				setGeneralTabValues();
				if (!"configuration".equals(tabname)) {
					buttonSaveDPU.setEnabled(false);
				}
				// refresh configuration
				configureDPUDialog();
			}
		});
		buttonDpuBar.addComponent(buttonSaveDPU);
		buttonDpuBar.setComponentAlignment(buttonSaveDPU,
				Alignment.BOTTOM_RIGHT);
		dpuDetailLayout.addComponent(buttonDpuBar);

		return buttonDpuBar;
	}

	/**
	 * Set values to components
	 * {@link #dpuName}, {@link #dpuDescription}, {@link #groupVisibility}
	 */
	public void setGeneralTabValues() {

		String selectedDpuName = selectedDpuWrap.getDPUTemplateRecord().getName();
		String selecteDpuDescription = selectedDpuWrap.getDPUTemplateRecord().getDescription();
		VisibilityType selecteDpuVisibility = selectedDpuWrap.getDPUTemplateRecord().getVisibility();
		dpuName.setValue(selectedDpuName.trim());
		dpuName.setReadOnly(!listener.hasPermission("save"));
		dpuDescription.setValue(selecteDpuDescription.trim());
		dpuDescription.setReadOnly(!listener.hasPermission("save"));

		groupVisibility.setValue(selecteDpuVisibility);
		groupVisibility.setEnabled(true);
		if (selecteDpuVisibility == VisibilityType.PUBLIC) {
			groupVisibility.setValue(selecteDpuVisibility);
			groupVisibility.setEnabled(false);
		} else {
			groupVisibility.setValue(selecteDpuVisibility);
			groupVisibility.setEnabled(true);
			groupVisibility.addValueChangeListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(Property.ValueChangeEvent event) {
					buttonSaveDPU.setEnabled(listener.hasPermission("save"));
				}
			});
		}
	}

	/**
	 * Builds layout contains General tab of {@link #tabSheet}. Calls from
	 * {@link #buildDPUDetailLayout}
	 *
	 * @return verticalLayoutData VerticalLayout with all components of General
	 * tab.
	 */
	private VerticalLayout buildVerticalLayoutData() {

		// common part: create layout
		verticalLayoutData = new VerticalLayout();
		verticalLayoutData.setImmediate(false);
		verticalLayoutData.setWidth("100.0%");
		verticalLayoutData.setHeight("100%");
		verticalLayoutData.setMargin(true);

		//Layout contains name description and visibility of DPU Template
		GridLayout dpuSettingsLayout = new GridLayout(2, 5);
		dpuSettingsLayout.setStyleName("dpuSettingsLayout");
		dpuSettingsLayout.setMargin(true);
		dpuSettingsLayout.setSpacing(true);
		dpuSettingsLayout.setWidth("100%");
		dpuSettingsLayout.setHeight("100%");
		dpuSettingsLayout.setColumnExpandRatio(0, 0.10f);
		dpuSettingsLayout.setColumnExpandRatio(1, 0.90f);

		//Name of DPU Template: label & TextField
		Label nameLabel = new Label("Name:");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuSettingsLayout.addComponent(nameLabel, 0, 0);
		dpuName = new TextField();
		dpuName.setImmediate(true);
		dpuName.setWidth("200px");
		dpuName.setHeight("-1px");
		//settings of mandatory
		dpuName.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws Validator.InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				throw new Validator.InvalidValueException("Name must be filled!");
			}
		});
		dpuName.addValidator(new MaxLengthValidator(MaxLengthValidator.DPU_NAME_LENGTH));
		dpuSettingsLayout.addComponent(dpuName, 1, 0);
		dpuName.addTextChangeListener(new FieldEvents.TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(FieldEvents.TextChangeEvent event) {
				buttonSaveDPU.setEnabled(listener.hasPermission("save"));
			}
		});

		//Description of DPU Template: label & TextArea
		Label descriptionLabel = new Label("Description:");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuSettingsLayout.addComponent(descriptionLabel, 0, 1);
		dpuDescription = new TextArea();
		dpuDescription.addValidator(new MaxLengthValidator(MaxLengthValidator.DESCRIPTION_LENGTH));
		dpuDescription.setImmediate(true);
		dpuDescription.setWidth("100%");
		dpuDescription.setHeight("60px");
		dpuDescription.addTextChangeListener(new FieldEvents.TextChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void textChange(FieldEvents.TextChangeEvent event) {
				buttonSaveDPU.setEnabled(listener.hasPermission("save"));
			}
		});
		dpuSettingsLayout.addComponent(dpuDescription, 1, 1);

		//Visibility of DPU Template: label & OptionGroup
		Label visibilityLabel = new Label("Visibility:");
		dpuSettingsLayout.addComponent(visibilityLabel, 0, 2);
		groupVisibility = new OptionGroup();
		groupVisibility.addStyleName("horizontalgroup");
		groupVisibility.addItem(VisibilityType.PRIVATE);
		groupVisibility.addItem(VisibilityType.PUBLIC);
		dpuSettingsLayout.addComponent(groupVisibility, 1, 2);

		// JAR path of DPU Template.
		HorizontalLayout jarPathLayout = new HorizontalLayout();
		jarPathLayout.setImmediate(false);
		jarPathLayout.setSpacing(true);
		jarPathLayout.setHeight("100%");
		dpuSettingsLayout.addComponent(new Label("JAR path:"), 0, 3);
		jarPath = new Label(selectedDpuWrap.getDPUTemplateRecord().getJarPath());

		//reload JAR file button
		fileUploadReceiver = new FileUploadReceiver();
		reloadFile = new Upload(null, fileUploadReceiver);
		reloadFile.setImmediate(true);
		reloadFile.setButtonCaption("Replace");
		reloadFile.addStyleName("horizontalgroup");
		reloadFile.setHeight("40px");
		reloadFile.setEnabled(listener.hasPermission("save"));
		reloadFile.addStartedListener(new Upload.StartedListener() {
			/**
			 * Upload start listener. If selected file has JAR extension then an
			 * upload status window with upload progress bar will be shown. If
			 * selected file has other extension, then upload will be
			 * interrupted and error notification will be shown.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadStarted(final Upload.StartedEvent event) {
				String filename = event.getFilename();
				String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				String jar = "jar";

				if (!jar.equals(extension)) {
					reloadFile.interruptUpload();
					errorExtension = true;
					Notification.show("Selected file is not .jar file", Notification.Type.ERROR_MESSAGE);
					return;
				}

				if (uploadInfoWindow.getParent() == null) {
					UI.getCurrent().addWindow(uploadInfoWindow);
				}
				uploadInfoWindow.setClosable(false);
			}
		});


		reloadFile.addSucceededListener(new AuthAwareUploadSucceededWrapper(new Upload.SucceededListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadSucceeded(Upload.SucceededEvent event) {
				uploadInfoWindow.close();
				if (!errorExtension) {
					listener.dpuUploaded(fileUploadReceiver.getFile());
				}
			}
		}));


		reloadFile.addFailedListener(new Upload.FailedListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFailed(Upload.FailedEvent event) {
				uploadInfoWindow.close();
				if (errorExtension) {
					errorExtension = false;
				}

				Notification.show("Uploading " + event.getFilename() + " failed.", Notification.Type.ERROR_MESSAGE);
			}
		});

		// Upload status window
		uploadInfoWindow = new UploadInfoWindow(reloadFile);

		jarPathLayout.addComponent(jarPath);
		jarPathLayout.addComponent(reloadFile);
		dpuSettingsLayout.addComponent(jarPathLayout, 1, 3);

		// Description of JAR of DPU Template.
		dpuSettingsLayout.addComponent(new Label("Description of JAR:"), 0, 4);
		TextArea jDescription = new TextArea(selectedDpuWrap.getDPUTemplateRecord().getJarDescription());
		jDescription.setReadOnly(true);
		jDescription.setWidth("100%");
		jDescription.setHeight("100%");
		dpuSettingsLayout.addComponent(jDescription, 1, 4);

		verticalLayoutData.addComponent(dpuSettingsLayout);

		return verticalLayoutData;
	}

	@Override
	public void refresh() {
		dpuTree.refresh();
	}

	@Override
	public boolean isChanged() {
		boolean configChanged = false;
		try {
			configChanged = selectedDpuWrap.getDialog().hasConfigChanged();
		} catch (FileNotFoundException e) {
			Notification.show(
					"File not found. ",
					e.getMessage(), Notification.Type.ERROR_MESSAGE);
			LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), e);
		} catch (ModuleException e) {
			Notification.show(
					"Module Exception. ",
					e.getMessage(), Notification.Type.ERROR_MESSAGE);
			LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), e);
		} catch (DPUWrapException e) {
			Notification.show(
					"DPUWrap Exception. ",
					e.getMessage(), Notification.Type.ERROR_MESSAGE);
			LOG.error("Can't load DPU '{}'", selectedDpuWrap.getDPUTemplateRecord().getId(), e);
		}

		DPUTemplateRecord selectedDpu = selectedDpuWrap.getDPUTemplateRecord();

		if (!dpuName.getValue().equals(selectedDpu.getName())) {
			return true;
		} else if (!dpuDescription.getValue().equals(selectedDpu.getDescription())) {
			return true;
		} else if (!groupVisibility.getValue().equals(selectedDpu.getVisibility())) {
			return true;
		} else if (!jarPath.getValue().equals(selectedDpu.getJarPath())) {
			return true;
		} else if (configChanged) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Store DPU Template record to DB
	 */
	@Override
	public void saveDPUTemplate() {
		//control of the validity of Name field.
		if (!validate()) {
			//Notification.show("Failed to save DPURecord", "Mandatory fields should be filled", Notification.Type.ERROR_MESSAGE);
			return;
		}
		//saving Name, Description and Visibility
		if ((selectedDpuWrap != null)
				&& (selectedDpuWrap.getDPUTemplateRecord().getId() != null)) {
			selectedDpuWrap.getDPUTemplateRecord().setName(dpuName.getValue().trim());
			selectedDpuWrap.getDPUTemplateRecord().setDescription(dpuDescription
					.getValue().trim());
			selectedDpuWrap.getDPUTemplateRecord()
					.setVisibility((VisibilityType) groupVisibility
					.getValue());
			listener.saveDPU(selectedDpuWrap);
		}
	}

	@Override
	public void selectNewDPU(DPUTemplateRecord dpu) {
		//If DPURecord that != null was selected then it's details will be shown.
		if ((dpu != null) && (dpu.getId() != null)) {
			// crate new wrap
			selectedDpuWrap = new DPUTemplateWrap(dpu);

			dpuLayout.removeComponent(dpuDetailLayout);
			dpuLayout.removeComponent(layoutInfo);
			dpuDetailLayout = buildDPUDetailLayout();
			dpuLayout.addComponent(dpuDetailLayout, 1, 0);

			// show/hide replace button
			reloadFile.setVisible(
					selectedDpuWrap.getDPUTemplateRecord().jarFileReplacable());

			setGeneralTabValues();
			//Otherwise, the information layout will be shown.
		} else {
			dpuLayout.removeComponent(dpuDetailLayout);
			dpuLayout.removeComponent(layoutInfo);
			dpuLayout.addComponent(layoutInfo, 2, 0);

		}
	}

	/**
	 * Builds layout contains DPU instances tab of {@link #tabSheet}. Calls from
	 * {@link #buildDPUDetailLayout}
	 *
	 * @return verticalLayoutInstances VerticalLayout with all components of DPU
	 * instances tab.
	 */
	private VerticalLayout buildVerticalLayoutInstances() {

		// common part: create layout
		verticalLayoutInstances = new VerticalLayout();
		verticalLayoutInstances.setImmediate(false);
		verticalLayoutInstances.setWidth("100.0%");
		verticalLayoutInstances.setMargin(true);

		tableData = listener.getTableData();

		//Table with instancesof DPU
		instancesTable = new IntlibPagedTable();
		instancesTable.setSelectable(true);
		instancesTable.setCaption("Pipelines:");
		instancesTable.setContainerDataSource(tableData);

		//sorting by id
		Object property = "id";
		instancesTable.setSortContainerPropertyId(property);
		instancesTable.setSortAscending(true);
		instancesTable.sort();

		instancesTable.setWidth("100%");
		instancesTable.setImmediate(true);
//		instancesTable.setVisibleColumns((Object[]) visibleCols);
//		instancesTable.setColumnHeaders(headers);

		instancesTable.addGeneratedColumn("actions",
				new ActionColumnGenerator());

		verticalLayoutInstances.addComponent(instancesTable);
		verticalLayoutInstances.addComponent(instancesTable.createControls());
		instancesTable.setFilterFieldVisible("actions", false);
		instancesTable.setPageLength(6);

		return verticalLayoutInstances;
	}

	public boolean validate() {
		try {
			dpuName.validate();
			dpuDescription.validate();
		} catch (Validator.InvalidValueException e) {
			Notification.show("Error validating DPU", e.getMessage(), Notification.Type.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	/**
	 * Load configuration into DPU's configuration dialog. In case of exception
	 * show notification.
	 */
	private void configureDPUDialog() {
		// refresh configuration
		try {
			selectedDpuWrap.configuredDialog();
		} catch (ConfigException e) {
			Notification.show(
					"Configuration problem",
					e.getMessage(), Notification.Type.WARNING_MESSAGE);
			LOG.error("Failed to load configuration for {}", selectedDpuWrap.getDPUTemplateRecord().getId(), e);
		} catch (DPUWrapException e) {
			Notification.show(
					"Unexpected error. The configuration dialog may not be loaded correctly.",
					e.getMessage(), Notification.Type.WARNING_MESSAGE);
			LOG.error("Unexpected error while loading dialog for {}", selectedDpuWrap.getDPUTemplateRecord().getId(), e);
		}
	}

	/**
	 * Generate column in table {@link #instancesTable}. with buttons:Detail,
	 * Delete, Status.
	 *
	 * @author Maria Kukhar
	 *
	 */
	class ActionColumnGenerator implements
			com.vaadin.ui.CustomTable.ColumnGenerator {

		private static final long serialVersionUID = 1L;

		@Override
		public Object generateCell(final CustomTable source,
				final Object itemId, Object columnId) {

			final Long pipeId = (Long) tableData.getContainerProperty(itemId, "id").getValue();

			HorizontalLayout layout = new HorizontalLayout();
			//Detail button
			Button detailButton = new Button();
			detailButton.setCaption("Detail");
			detailButton.setWidth("70px");
			detailButton
					.addClickListener(new com.vaadin.ui.Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(Button.ClickEvent event) {
					listener.pipelineAction(pipeId, "detail");
				}
			});
			layout.addComponent(detailButton);


			//Delete button. Delete pipeline.
			Button deleteButton = new Button();
			deleteButton.setCaption("Delete");
			deleteButton.setWidth("70px");
			deleteButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(Button.ClickEvent event) {
					listener.pipelineAction(pipeId, "delete");
					// now we have to remove pipeline from table
					source.removeItem(itemId);
				}
			});
			layout.addComponent(deleteButton);

			//Status button
			Button statusButton = new Button();
			statusButton.setCaption("Status");
			statusButton.setWidth("70px");
			statusButton.addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(Button.ClickEvent event) {
					listener.pipelineAction(pipeId, "status");
				}
			});
			layout.addComponent(statusButton);

			return layout;
		}
	}
}