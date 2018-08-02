import React, { Component } from 'react';
import './DateEntry.css';

class DateEntry extends Component {

	accept = () => {
		this.props.onAccept(this.props.id);
	}

	reject = () => {
		this.props.onReject(this.props.id);
	}

	render() {
		const accepted = this.props.accepted ? " accepted" : "";
		const rejected = this.props.rejected ? " rejected" : "";

		return (
			<div className={"entry"}>
				<div className={"item date"}>{this.props.date}</div>
				<div className={"item button accept" + accepted} onClick={this.accept}>akzeptiert</div>
				<div className={"item button reject" + rejected} onClick={this.reject}>abgelehnt</div>
				<div className={"item stats"}>
					<div className={"detail pending"}></div>
					<div className={"detail accepted"}>{this.props.acceptedCount}</div>
					<div className={"detail rejected"}>{this.props.rejectedCount}</div>
				</div>
			</div>

		);
	}
}

export default DateEntry;
