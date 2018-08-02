import React, {Component} from 'react';
import './InputField.css';

class InputField extends Component {

  render() {
    return (<input value={this.props.code} onChange={this.props.onChange} className={"auth"} type={"password"} maxLength={12} />);
  }
}

export default InputField;
