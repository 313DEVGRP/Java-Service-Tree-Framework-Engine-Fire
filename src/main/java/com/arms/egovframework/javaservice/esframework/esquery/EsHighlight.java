package com.arms.egovframework.javaservice.esframework.esquery;

import com.arms.egovframework.javaservice.esframework.EsQuery;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.util.List;

public class EsHighlight extends EsQuery {

	private final HighlightBuilder highlightBuilder = new HighlightBuilder();

	public EsHighlight(List<String> fields){
		fields.forEach(
			this.highlightBuilder::field
		);
	}

	public EsHighlight() {
		this.highlightBuilder.field("*").preTags("<em>").postTags("</em>");
	}

	public HighlightBuilder getHighlight() {
		return this.highlightBuilder;
	}
}
