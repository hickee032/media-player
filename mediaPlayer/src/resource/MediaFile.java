package resource;

//파일 경로, 표시 이름, 제목 저장

public class MediaFile {
	
	String fileName;

	String filePath;
	
	public MediaFile(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	

}
