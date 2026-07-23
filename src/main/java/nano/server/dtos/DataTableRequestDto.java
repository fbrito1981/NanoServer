package nano.server.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class DataTableRequestDto {
	private int draw;
	
	private int start;
	
	private int length;
	
	private Search search;
	
	private List<Order> order;
	
	private List<Column> columns;
	
	public DataTableRequestDto(HttpServletRequest request) throws Exception {
		this.draw = Integer.valueOf(request.getParameter("draw"));
		this.start = Integer.valueOf(request.getParameter("start"));
		this.length = Integer.valueOf(request.getParameter("length"));
		
		this.search = new Search(request);
		
		this.order = new ArrayList<>();

		int counter = 0;
		Order newOrder = new Order(request, counter);
		while (newOrder.isValid()) {
			this.order.add(newOrder);
			counter++;
			newOrder = new Order(request, counter);
		}
		
		this.columns = new ArrayList<>();

		counter = 0;
		Column column = new Column(request, counter);
		while (column.isValid()) {
			this.columns.add(column);
			counter++;
			column = new Column(request, counter);
		}
	}
	
	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public static class Search {
		private String value;
		private boolean regex;

		public Search(HttpServletRequest request) throws Exception {
			this.value = request.getParameter("search[value]");
			this.regex = Boolean.valueOf(request.getParameter("search[regex]"));
		}

		public Search(HttpServletRequest request, int index) throws Exception {
			String valueName = String.format(Locale.getDefault(),
					"columns[%d][search][value]", index);
			String regexName = String.format(Locale.getDefault(),
					"columns[%d][search][regex]", index);
			this.value = request.getParameter(valueName);
			this.regex = Boolean.valueOf(request.getParameter(regexName));
		}
		
		public String getValue() {
			return value;
		}

		public boolean isRegex() {
			return regex;
		}
	}
	
	public static class Order {
		private int column;
		private String dir;

		public Order(HttpServletRequest request, int index) throws Exception {
			String columnName = String.format(Locale.getDefault(),
					"order[%d][column]", index);
			String dirName = String.format(Locale.getDefault(),
					"order[%d][dir]", index);
			String columnValue = request.getParameter(columnName);
			if (columnValue != null) {
				this.column = Integer.valueOf(columnValue);
				this.dir = request.getParameter(dirName);
			} else {
				this.column = -1;
			}
		}
		
		public int getColumn() {
			return column;
		}

		public String getDir() {
			return dir;
		}
		
		public boolean isValid() {
			return column >= 0;
		}
	}
	
	public static class Column {
		private String data;
		
		private String name;
		
		private boolean searchable;
		
		private boolean orderable;
		
		private Search search;

		public Column(HttpServletRequest request, int index) throws Exception {
			String dataName = String.format(Locale.getDefault(),
					"columns[%d][data]", index);
			String nameName = String.format(Locale.getDefault(),
					"columns[%d][name]", index);
			String searchableName = String.format(Locale.getDefault(),
					"columns[%d][searchable]", index);
			String orderableName = String.format(Locale.getDefault(),
					"columns[%d][orderable]", index);
			String dataValue = request.getParameter(dataName);
			if (dataValue != null) {
				this.data = dataValue;
				this.name = request.getParameter(nameName);
				this.searchable = Boolean.valueOf(request.getParameter(searchableName));
				this.orderable = Boolean.valueOf(request.getParameter(orderableName));
				this.search = new Search(request, index);
			}
		}
		
		public String getData() {
			return data;
		}

		public String getName() {
			return name;
		}

		public boolean isSearchable() {
			return searchable;
		}

		public boolean isOrderable() {
			return orderable;
		}

		public Search getSearch() {
			return search;
		}
		
		public boolean isValid() {
			return data != null;
		}
	}
}
