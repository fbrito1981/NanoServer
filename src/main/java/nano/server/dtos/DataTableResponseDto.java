package nano.server.dtos;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class DataTableResponseDto<T> {
	
	private int draw;
	private int recordsTotal;
	private int recordsFiltered;
	private List<T> data;
	private String error;

	public DataTableResponseDto(HttpServletRequest request, Throwable t) {
		try {
			this.draw = Integer.valueOf(request.getParameter("draw"));
		} catch (Exception e) {
			this.draw = 0;
		}
		this.recordsTotal = 0;
		this.recordsFiltered = 0;
		this.error = t.getMessage();
	}
	
	public DataTableResponseDto(int draw, int recordsTotal, List<T> data) throws Exception {
		this(draw, recordsTotal, data.size(), data, null);
	}

	public DataTableResponseDto(int draw, int recordsTotal, int recordsFiltered, List<T> data, String error) throws Exception {
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
		this.error = error;
	}
	
	public int getDraw() {
		return draw;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public List<T> getData() {
		return data;
	}

	public String getError() {
		return error;
	}
}
