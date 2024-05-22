package com.arms.elasticsearch.query.esquery;

import com.arms.elasticsearch.query.EsQuery;
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
