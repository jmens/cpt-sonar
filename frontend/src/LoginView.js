import React, { Component } from 'react';
import InputField from './InputField';
import logo from './img/cpt-sonar-logo.png';

import './LoginView.css';

class LoginView extends Component {

	login = (code) => {
		this.props.onLogin(code);
	};

	renderWarning = () => {
		if (this.props.accesDenied === 'denied') {
			return (<div className={"access-denied"}>Zugriff verweigert</div>)
		} else if (this.props.accesDenied === 'pending') {
			return (<div className={"access-pending"}>Authentifiziere</div>)
		} else {
			return null;
		}
	}

	render() {
		const authButtonClasses = this.props.accesDenied === 'denied' || this.props.accesDenied === 'pending'
			? "item button authenticating"
			: "item button";

		console.log('authclasses', authButtonClasses);
		console.log('authclasses', this.props.accesDenied);

		return (
			<div className="unauthenticated">
				<div className="row row-header">
					<div className={"row-content header"}>
						<h1 className="title">Captain Sonar - Einsatzplanung</h1>
						<h2>Authentifizierung erforderlich</h2>
					</div>
				</div>
				<div className="row row-login">
					<div className={"row-content login"}>
						<div className={"label"}>Bitte Code eingeben:</div>
						<InputField code={this.props.code} onChange={this.props.changeCode}/>
						<div className={"button-row"}>
							<div className={authButtonClasses} onClick={this.login}>authentifizieren</div>
							{ this.renderWarning() }
						</div>
					</div>
				</div>
				<div className="row row-footer">
					<div className={"row-content footer"}>
						<img className={"logo"} src={logo} alt="" />
					</div>
				</div>
			</div>);
	}
}

export default LoginView;
