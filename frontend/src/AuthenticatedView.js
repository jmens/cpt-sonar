import React, { Component } from 'react';
import logo from './img/logo.jpg';
import table from './img/player-table-trans.png';
import DateEntry from './DateEntry';

import './AuthenticatedView.css';

class AuthenticatedView extends Component {

	renderSchedule = () => {
		const schedules = this.props.schedule;
		return (schedules && typeof schedules !== 'string')
		? schedules.map((schedule, i) => (
			<DateEntry
				key={schedule.id}
				id={schedule.id}
				date={schedule.date}
				accepted={schedule.accepted}
				rejected={schedule.rejected}
				pendingCount={schedule.pendingCount}
				acceptedCount={schedule.acceptedCount}
				rejectedCount={schedule.rejectedCount}
				onAccept={() => this.props.onAccept(schedule.id)}
				onReject={() => this.props.onReject(schedule.id)}
			/>))
			: null;
	};

	render() {
		return (
			<div className="authenticated">
				<div className="row row-header">
					<div className={"row-content header"}>
						<h1 className="title">Captain Sonar - Einsatzplanung</h1>
						<h2>Willkommen Kapitän { this.props.captain.name }</h2>
					</div>
				</div>
				<div className={"row row-footer"}>
					<div className={"row-content footer"}>
						<img className={"img-logo"} src={logo} alt={"Captain Sonar Action"} />
					</div>
				</div>
				<div className={"row row-intro"}>
					<div className={"row-content intro"}>
						<p className="App-intro">Das Oberkommando plant die Jungfernfahrt
							zweier Boote. Die globale Situation öffnet dazu mehrere
							Zeitfenster.</p>
						<p className="App-intro">Bitte wählen Sie Termine für den Stapellauf
							aus:</p>
					</div>
				</div>
				<div className={"row row-dates"}>
					<div className={"row-content dates"}>
						<div className={"dates-container"}>
							{this.renderSchedule()}
						</div>
					</div>
				</div>
				<div className={"row row-about"}>
					<div className={"row-content about"}>
						<p>Ziel der Übung sind mehrere Runden "Captain Sonar". Die Aufzeichnung einer vergleichbaren Fahrt finden Sie weiter unten.</p>
						<p>Wenn es zu einem Termin mit voller Mannschaftsstärke kommt, wird das Oberkommando
							Einladungen mit allen weiteren Informationen verschicken.</p>
						<p>Sollten Sie Fragen haben, kein Kapitän sein oder keine Ahnung haben, worum es hier geht:
							Bitte kurze Nachricht an oberkommando@jmens.de.</p>
					</div>
				</div>
				<div className={"row row-media"}>
					<div className={"row-content media"}>
						<img className={"img-table"} src={table} alt={"Captain Sonar Players"} />
					</div>
				</div>
				<div className={"row row-video"}>
					<div className={"row-content video"}>
						<iframe width="100%" height="450" src="https://www.youtube.com/embed/np_VvcfTTlE" frameBorder="0" allow="autoplay; encrypted-media" allowFullScreen title={"Demo Spiel"}/>
					</div>
				</div>
			</div>
		);
	}
}

export default AuthenticatedView;
