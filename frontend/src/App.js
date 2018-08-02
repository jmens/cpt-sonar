import React, { Component } from 'react';
import AuthenticatedView from './AuthenticatedView';
import LoginView from './LoginView';
import axios from 'axios';

import './App.css';

import fetch from 'node-fetch';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || '';

class App extends Component {

	state = {
		token: '',
		code: '',
		captain: null,
		schedule: null,
		access_denied: ''
	};

	updateCode = (element) => this.setState({code: element.target.value});

	login = (code) => {

		this.setState({access_denied: 'pending'});

		const that = this;
		axios({
			url: API_BASE_URL + '/authenticate',
			method: 'POST',
			data: {accessKey: that.state.code}
		}).then((response) => {
			if (response.status === 200) {
				that.setState({token: response.data.token, access_denied: ''}, () => {
					that.fetchAuthenticatedCaptain();
					that.fetchSchedule();
				});
			} else {
				that.setState({access_denied: 'denied'}, () => setTimeout(5000, () => that.setState({access_denied: ''})));
			}
		}).catch(() => {
			that.setState({access_denied: 'denied'}, () => window.setTimeout(() => {
				that.setState({access_denied: ''});
			}, 2500));
		});
	};

	fetchAuthenticatedCaptain = () => {
		if (!this.state.captain) {
			const that = this;
			axios({
				url: API_BASE_URL + '/captain',
				method: 'GET',
				headers: { 'X-APP-SECURITY': that.state.token }
			}).then(function (res) {
				that.setState({captain: (res.status === 200) ? res.data : null});
				return res.status;
			}).catch(() => 500)
		}
	};

	reject = (id) => this.updateDecision(id, 'reject');

	accept = (id) => this.updateDecision(id, 'accept');

	updateDecision = (id, type) => {
		const that = this;

		const accept = type === 'accept';
		axios({
			url: accept ? API_BASE_URL + '/accept' : API_BASE_URL + '/reject',
			method: 'POST',
			headers: {
				'X-APP-SECURITY': that.state.token
			},
			data: { id }
		}).then(response => {
			if (response.status === 200) {
				that.fetchSchedule(true);
			}
		});
	};

	fetchSchedule = (force = false) => {
		const that = this;
		if (!this.state.schedule || force) {
			fetch(API_BASE_URL + '/schedule', {
				headers: {
					'X-APP-SECURITY': that.state.token
				}
			})
			.then(res => res.json())
			.catch(() => "FAILED")
			.then((body) => that.setState({ schedule: body }))
		}
	};

	render() {
		return (<div className={"app"}>
			{ ((this.state.captain)
				? <AuthenticatedView schedule={this.state.schedule} captain={this.state.captain} onAccept={this.accept} onReject={this.reject} />
				: <LoginView onLogin={this.login} accesDenied={this.state.access_denied} code={this.state.code} changeCode={this.updateCode}/>) }
		</div>);
	};
}

export default App;
