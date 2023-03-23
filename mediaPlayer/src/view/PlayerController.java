package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import MediaPlayer.Main;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import resource.FileExtensionRead;
import resource.MediaFile;
import resource.MusicFile;
import resource.VideoFile;

public class PlayerController implements Initializable {

	private Stage primaryStage;

	/* 메인 (Player) @FXML----- */
	// Pane
	@FXML
	private SplitPane splitepane;
	@FXML
	private VBox AlistPane;
	@FXML
	private AnchorPane PlaySection, MediaCr;
	@FXML
	private StackPane stackPane;
	@FXML
	private HBox SliderBox, ControlBox;

	// Label
	@FXML
	private Label NoFile, FileListLength, mediaTimePlayed, mediaTimeEnd;
	// Slider
	@FXML
	private Slider sliderMedia, sliderVolume;

	// ImageView,MediaView
	@FXML
	private ImageView imageView;
	@FXML
	private MediaView mediaView;

	// button (Button,ToggleButton)
	@FXML
	private Button btnPlay, btnPause, btnStop, btnBack, btnFoward, Prevbtn, Nextbtn, SoundBtn;
	@FXML
	private ToggleButton ListOpen;

	/* 리스트 (List) @FXML----- */

	// ListView
	@FXML
	private ListView<String> playList;

	// Button
	@FXML
	private Button listAddBtn, listDeleteBtn, listCloseBtn;

	ArrayList<MediaFile> MediaList = new ArrayList<>();
	ArrayList<MusicFile> musicList = new ArrayList<>();
	ArrayList<VideoFile> videosList = new ArrayList<>();

//	boolean widthListenerSet = false;
//	boolean heightListenerSet = false;

	final int secondsInMinute = 60;
	final int secondsInHour = 3600;

	int currentTrackToHoursPlayed;
	int currentTrackToMinsPlayed;
	int currentTrackToSecsPlayed;

	int currentTrackToHoursEnd;
	int currentTrackToMinsEnd;
	int currentTrackToSecsEnd;

	private boolean endOfMedia;
	boolean browsingMediaSeeker = false;
	boolean isPlaying = false;

	Stage listStage;
	Image image;
	double oldVolume;

	boolean volumeReset = false;

	private MediaPlayer mediaPlayer;

	@SuppressWarnings("unused")
	private boolean listPaneOpen = false;
	@SuppressWarnings("unused")
	private Scene playlistScene;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listPaneOpen = false;
		splitepane.getItems().remove(AlistPane);
		sliderVolume.setValue(25);
		NoFile.setVisible(false);
		conText();
		sizeControl();
		BasicBtnAction();
	}

	@FXML
	public void openPlaylist(ActionEvent event) {

		if (!getListOpen().isSelected()) {
			listPaneOpen = false;
			splitepane.getItems().remove(AlistPane);
			try {
				Main.PrimaryStageMinsize(primaryStage, 720.0, 437.0);
			} catch (Exception e) {
				FileListLength.setText(String.valueOf(MediaList.size()));
			}
			System.out.println("close");
		} else {
			listPaneOpen = true;
			splitepane.getItems().add(1, AlistPane);
			try {
				Main.PrimaryStageMinsize(primaryStage, 1028.0, 437.0);
			} catch (Exception e) {
				FileListLength.setText(String.valueOf(MediaList.size()));
			}
			System.out.println("open");
		}
	}

	public void bringPlayListRoot(Parent playlistRoot) {
		playlistScene = new Scene(playlistRoot);
	}

//ContextMenu
	public void conText() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuOpen = new MenuItem("File OPEN");
		menuOpen.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));

		menuOpen.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				fileOpen();
				try {
					PlayPauseBtnActive();
					NoFile.setVisible(false);
				} catch (NullPointerException e) {
					imageView.setVisible(true);
					NoFile.setVisible(true);
					PlayPauseBtnActive();
				}
			}
		});

		MenuItem menuFopen = new MenuItem("Folder Open");
		menuFopen.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));

		Menu parentMenu = new Menu("Play speed");
		MenuItem childMenuItem1 = new MenuItem("Fast▲0.5");
		MenuItem childMenuItem2 = new MenuItem("Slow▼0.5");
		parentMenu.getItems().addAll(childMenuItem1, childMenuItem2);

		CheckMenuItem checkMenuMute = new CheckMenuItem("Mute");
		checkMenuMute.setSelected(false);
		checkMenuMute.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (checkMenuMute.isSelected()) {
					mediaPlayer.setMute(true);
				} else if (!checkMenuMute.isSelected()) {
					mediaPlayer.setMute(false);
				}
			}
		});

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

		RadioMenuItem radioMenuItem1 = new RadioMenuItem("Radio - Option 1");
		RadioMenuItem radioMenuItem2 = new RadioMenuItem("Radio - Option 2");
		ToggleGroup group = new ToggleGroup();

		radioMenuItem1.setToggleGroup(group);
		radioMenuItem2.setToggleGroup(group);

		contextMenu.getItems().addAll(menuOpen, menuFopen, parentMenu, checkMenuMute, separatorMenuItem, radioMenuItem1,
				radioMenuItem2);

		stackPane.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				if (contextMenu.isShowing()) {
					contextMenu.hide();
				} else {
					contextMenu.show(mediaView, event.getScreenX(), event.getScreenY());
				}
			}
		});
	}

	final String exeMp4 = "mp4";
	final String exeFlv = "flv";
	final String exeWav = "wav";
	final String exeMp3 = "mp3";

	MediaFile SelectMediaType(String fileExtension, MediaFile mediaFile, String filePath) {
		if (fileExtension.equals(exeMp4) || fileExtension.equals(exeFlv)) {
			return mediaFile = new VideoFile(filePath);
		} else if (fileExtension.equals(exeMp3) || fileExtension.equals(exeWav)) {
			return mediaFile = new MusicFile(filePath);
		} else {
			mediaFile = null;
			return mediaFile;
		}
	}

//Music 파일 확장자
	final FileChooser.ExtensionFilter extFilterAny = new FileChooser.ExtensionFilter("Any (*.*)", "*");
	final FileChooser.ExtensionFilter extFilterAiff = new FileChooser.ExtensionFilter("AIFF (*.aiff)", "*.aiff");
	final FileChooser.ExtensionFilter extFilterMp3 = new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3");
	final FileChooser.ExtensionFilter extFilterWav = new FileChooser.ExtensionFilter("WAV (*.wav)", "*.wav");

//Video 파일 확장자
	final FileChooser.ExtensionFilter extFilterFLV = new FileChooser.ExtensionFilter("FLV (*.flv)", "*.flv");
	final FileChooser.ExtensionFilter extFilterMpeg4 = new FileChooser.ExtensionFilter("MPEG4 (*.mp4)", "*.mp4");

	public String getFilepathForMediaFile(File file) {

		if (file != null) {
			String filePath = file.toURI().toString();
			return filePath;
		} else {
			System.err.println("파일이 없음 (취소됨)");
			return null;
		}
	}

//파일 열기
	public void fileOpen() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(extFilterAny, extFilterAiff, extFilterMp3, extFilterWav, extFilterFLV,
				extFilterMpeg4);
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
		String filePath = getFilepathForMediaFile(selectedFile);
		if (filePath != null && !filePath.isEmpty()) {
			MediaFile tempMedia = null;
			String fileExtension = FileExtensionRead.getExtension(filePath);
			tempMedia = SelectMediaType(fileExtension, tempMedia, filePath);
			if (tempMedia != null) {
				Media media = new Media(tempMedia.getFilePath());
				tempMedia.setFileName(selectedFile.getName());

				disposeMediaplayerIfNeeded(mediaPlayer);

				mediaPlayer = new MediaPlayer(media);
				mediaAddArrayList(tempMedia);

				resetVolume(volumeReset);
				mediaView.setMediaPlayer(mediaPlayer);
				if (primaryStage == null) {
					primaryStage = (Stage) mediaView.getScene().getWindow();
					primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
				}
				mediaPlayer.seek(mediaPlayer.getStartTime());
				mediaPlayer.play();
				isPlaying = true;
				PlayPauseBtnActive();
				addVolumeListener();
				addTimeListener();
				sizeControl();
				FileListLength.setText(String.valueOf(MediaList.size()));
				playList.getSelectionModel().select(selectedFile.getName());

			} else {
				System.err.println("지원하지 않는 파일");
			}
		} else {
			System.err.println("추가된 미디어파일이 없음");
		}
	}

//사이즈 조절
	public void sizeControl() {
		imageView.fitWidthProperty().bind(Bindings.selectDouble(stackPane.sceneProperty(), "width"));
		imageView.fitHeightProperty().bind(Bindings.selectDouble(stackPane.sceneProperty(), "height"));

		mediaView.fitWidthProperty().bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		mediaView.fitHeightProperty().bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

		MediaCr.prefWidthProperty().bind(Bindings.selectDouble(stackPane.sceneProperty(), "width"));
		SliderBox.prefWidthProperty().bind(Bindings.selectDouble(stackPane.sceneProperty(), "width"));
		ControlBox.prefWidthProperty().bind(Bindings.selectDouble(stackPane.sceneProperty(), "width"));
	}

	public void ListviewOPen() {
		splitepane.getItems().add(1, AlistPane);
	}

	public void ListviewClose() {
		splitepane.getItems().remove(AlistPane);
	}

	public void addVolumeListener() {
		sliderVolume.setValue(mediaPlayer.getVolume() * 100);
		sliderVolume.valueProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				mediaPlayer.setVolume(sliderVolume.getValue() / 100);
			}
		});
	}

	public void addTimeListener() {
		mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				if (!sliderMedia.isValueChanging()) {
					sliderMedia.maxProperty().set(mediaPlayer.getTotalDuration().toSeconds());
					sliderMedia.setValue(newValue.toSeconds());

					currentTrackToHoursPlayed = (int) mediaPlayer.getCurrentTime().toSeconds() / secondsInHour;
					currentTrackToMinsPlayed = (int) (mediaPlayer.getCurrentTime().toSeconds() % secondsInHour)
							/ secondsInMinute;
					currentTrackToSecsPlayed = (int) mediaPlayer.getCurrentTime().toSeconds() % secondsInMinute;

					mediaTimePlayed.setText(String.format("%02d:%02d:%02d", currentTrackToHoursPlayed,
							currentTrackToMinsPlayed, currentTrackToSecsPlayed));

					currentTrackToHoursEnd = (int) mediaPlayer.getStopTime().toSeconds() / secondsInHour;
					currentTrackToMinsEnd = (int) (mediaPlayer.getStopTime().toSeconds() % secondsInHour)
							/ secondsInMinute;
					currentTrackToSecsEnd = (int) mediaPlayer.getStopTime().toSeconds() % secondsInMinute;

					mediaTimeEnd.setText(String.format("%02d:%02d:%02d", currentTrackToHoursEnd, currentTrackToMinsEnd,
							currentTrackToSecsEnd));
				}
			}
		});

	}

//슬라이더
	@FXML
	private void sliderMediaClickAction(MouseEvent event) {
		System.out.println("Click");
		if (mediaPlayer != null) {
			if (!browsingMediaSeeker) {
				mediaPlayer.pause();
				mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
				if (isPlaying) {
					mediaPlayer.play();
				}
			} else {
				browsingMediaSeeker = false;
			}
			PlayPauseBtnActive();
		}
	}

	@FXML
	private void sliderMediaPressedAction(MouseEvent event) {
		System.out.println("Pressed");
		if (mediaPlayer != null) {
			mediaPlayer.pause();
			PlayPauseBtnActive();
		}
	}

	@FXML
	private void sliderMediaReleasedAction(MouseEvent event) {
		System.out.println("Released");
		if (mediaPlayer != null) {
			if (isPlaying) {
				if (browsingMediaSeeker) {
					mediaPlayer.play();
				}
			}
			PlayPauseBtnActive();
		}
	}

	@FXML
	private void sliderMediaDraggedAction(MouseEvent event) {
		System.out.println("Dragged");
		if (mediaPlayer != null) {
			mediaPlayer.seek(Duration.seconds(sliderMedia.getValue()));
		}
	}

	@FXML
	public void MediaCrOPen() {
		MediaCr.setVisible(true);
	}

	@FXML
	public void MediaCrClose() {
		MediaCr.setVisible(false);
	}

	@FXML
	public void closeListBtn() {
		splitepane.getItems().remove(AlistPane);
		ListOpen.setSelected(false);
		sizeControl();
	}

//getter setter
	public ToggleButton getListOpen() {
		return ListOpen;
	}

	public void setListOpen(ToggleButton listOpen) {
		ListOpen = listOpen;
	}

//Property	
	public DoubleProperty sliderProgressWidthProperty() {
		return sliderMedia.prefWidthProperty();
	}

	public DoubleProperty mediaViewHeightProperty() {
		return mediaView.fitHeightProperty();
	}

	public DoubleProperty mediaViewWidthProperty() {
		return mediaView.fitWidthProperty();
	}

	@FXML
	void ListMouseClicked(MouseEvent event) {
		String filePath;

		if (event.getClickCount() == 2) {
			if (mediaPlayer != null) {
				oldVolume = mediaPlayer.getVolume();
				mediaPlayer.dispose();
			}

			if (playList.getSelectionModel().getSelectedItem() != null) {
				filePath = MediaList.get(playList.getSelectionModel().getSelectedIndex()).getFilePath();

			} else {
				System.err.println("선택된 파일이 없습니다.");
				return;
			}

			if (filePath != null && !filePath.isEmpty()) {
				Media media = new Media(filePath);

				mediaPlayer = new MediaPlayer(media);
				mediaPlayer.setVolume(oldVolume);

				mediaView.setMediaPlayer(mediaPlayer);
				mediaView.setPreserveRatio(true);

				mediaPlayer.seek(mediaPlayer.getStartTime());
				mediaPlayer.play();
				isPlaying = true;
				addTimeListener();
				sizeControl();
			} else {
				System.err.println("ERROR: No media to play.");
			}
		}
	}

	void resetVolume(boolean reset) {
		if (!reset) {
			mediaPlayer.setVolume(0.25f);
			volumeReset = true;
		} else {
			mediaPlayer.setVolume(oldVolume);
		}
	}

	void disposeMediaplayerIfNeeded(MediaPlayer mediaPlayer) {
		if (mediaPlayer != null) {
			oldVolume = mediaPlayer.getVolume();
			mediaPlayer.dispose();
		}
	}

	public void stageSetmin(double width, double height) {
		primaryStage.setMinWidth(width);
		primaryStage.setMinHeight(height);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////

	// button 바꾸기 재생시 일시정지시
	public void PlayPauseBtnActive() {
		if (isPlaying) {
			btnPause.setVisible(true);
			btnPlay.setVisible(false);

		} else {
			btnPause.setVisible(false);
			btnPlay.setVisible(true);
		}
	}

	// 마우스 더블 클릭시 전체 화면 전환 -> 다시 더블 클릭 원래크기로
	@FXML
	public void MediaviewClick(MouseEvent event) {
		if (mediaPlayer != null) {
			if (isPlaying) {
				mediaPlayer.pause();
				isPlaying = false;
			} else {
				mediaPlayer.play();
				isPlaying = true;
			}

			if (!primaryStage.isFullScreen()) {
				if (event.getClickCount() == 2) {

					primaryStage.setFullScreen(true);
					DoubleProperty width = mediaView.fitWidthProperty();
					width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
					DoubleProperty height = mediaView.fitHeightProperty();
					height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

					mediaView.autosize();
				}
			} else {
				if (event.getClickCount() == 2) {
					primaryStage.setFullScreen(false);
					mediaView.autosize();
				}
			}
			PlayPauseBtnActive();
		}
	}

	public void BasicBtnAction() {

		btnPlay.setOnAction(event -> {
			if (endOfMedia) {
				isPlaying = false;
				PlayPauseBtnActive();
				mediaPlayer.stop();
				mediaPlayer.seek(mediaPlayer.getStartTime());
				endOfMedia = false;
			}
			try {
				isPlaying = true;
				PlayPauseBtnActive();
				mediaPlayer.play();
				addTimeListener();
				addVolumeListener();
			} catch (Exception e) {
				System.out.println("NO File");
			}

		});

		btnPause.setOnAction(event -> {
			try {
				isPlaying = false;
				PlayPauseBtnActive();
				mediaPlayer.pause();

			} catch (Exception e) {
				System.out.println("NO File");
			}
		});

		btnStop.setOnAction(event -> {
			try {
				isPlaying = false;
				PlayPauseBtnActive();
				mediaPlayer.stop();

			} catch (Exception e) {
				System.out.println("NO File");
			}
		});
		btnBack.setOnAction(event -> {
			try {
				mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(mediaPlayer.getTotalDuration().divide(10)));
				mediaPlayer.play();
			} catch (Exception e) {
				System.out.println("NO File");
			}

		});
		btnFoward.setOnAction(event -> {
			try {
				mediaPlayer.seek(mediaPlayer.getCurrentTime().add(mediaPlayer.getTotalDuration().divide(10)));
				mediaPlayer.play();
			} catch (Exception e) {
				System.out.println("NO File");
			}
		});
	}

	@FXML
	private void FFbtnAction(ActionEvent event) {

	}

	// 미디어를 array에 추가
	public void mediaAddArrayList(MediaFile file) {
		if (file instanceof VideoFile) {
			playList.getItems().add(file.getFileName());
			videosList.add((VideoFile) file);
			MediaList.add(file);

		} else if (file instanceof MusicFile) {
			playList.getItems().add(file.getFileName());
			musicList.add((MusicFile) file);
			MediaList.add(file);
		}

	}

	// List View 제거 버튼 이벤트
	@FXML
	public void removeButtonAction(ActionEvent event) {
		int index = playList.getSelectionModel().getSelectedIndex();

		if (!playList.getItems().isEmpty() && !MediaList.isEmpty()) {
			playList.getItems().remove(index);
//			musicList.remove(index);
//			MediaList.remove(index);
		}
	}

	/* getter setter ----- */
	// primaryStage setter
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
