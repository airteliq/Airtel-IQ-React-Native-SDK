import React, { Component } from 'react';
// import { PropTypes } from 'prop-types';
import { requireNativeComponent, Platform, View } from 'react-native';

class EnxToolBarView extends Component {
  render() {
    return <ReactPlayer {...this.props} />;
  }
}
const viewPropTypes = View.propTypes;
EnxToolBarView.propTypes = {
//    streamId: PropTypes.string.isRequired, 
//    isLocal: PropTypes.string.isRequired, 
  ...viewPropTypes,
};

const toolBarName = Platform.OS === 'ios' ? 'EnxToolBarSwift' : 'EnxToolBarManager';
const ReactPlayer = requireNativeComponent(toolBarName, EnxToolBarView);
export default EnxToolBarView;